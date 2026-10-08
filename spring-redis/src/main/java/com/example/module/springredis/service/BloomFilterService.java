package com.example.module.springredis.service;

import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.RedisException;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.output.BooleanListOutput;
import io.lettuce.core.output.BooleanOutput;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.CommandArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.module.springredis.config.BloomCommand.BF_ADD;
import static com.example.module.springredis.config.BloomCommand.BF_EXISTS;
import static com.example.module.springredis.config.BloomCommand.BF_MADD;
import static com.example.module.springredis.config.BloomCommand.BF_RESERVE;
import static io.lettuce.core.ScriptOutputType.STATUS;
import static io.lettuce.core.codec.StringCodec.UTF8;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class BloomFilterService {

    @Value("${spring.data.redis.bloom.active-filter-name}")
    private String activeFilter;

    @Value("${spring.data.redis.bloom.temp-filter-name}")
    private String tempFilter;

    private final RedisCommands<String, String> redisCommands;

    /**
     * Number of wallets sent to Redis per BF.MADD call. 1_000 - 10_000 is a good range.
     */
    private static final int BF_BATCH_SIZE = 5_000;
    private static final String BF_CAPACITY = "10000000";
    private static final String BF_FALSE_POSITIVE_RATE = "0.001";

    /**
     * Lua swap: UNLINK frees the old (large) key in a background thread instead of
     * blocking Redis, then RENAME promotes the temp filter. Scripts are atomic, so
     * readers never observe a missing key.
     */
    private static final String SWAP_SCRIPT = """
            redis.call('UNLINK', KEYS[1])
            redis.call('RENAME', KEYS[2], KEYS[1])
            return 'OK'
            """;

    private static final String WALLET_QUERY = """
            select ad.walletNumber
            from AutoDebit ad
            where ad.expiryDate >= :currentDate and ad.closed = :closed
            """;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeFilter() {
        try {
            redisCommands.dispatch(
                    BF_RESERVE,
                    new StatusOutput<>(UTF8),
                    new CommandArgs<>(UTF8)
                            .add(activeFilter)
                            .add(BF_FALSE_POSITIVE_RATE)
                            .add(BF_CAPACITY)
            );

            log.info("Bloom filter initialized");

        } catch (RedisCommandExecutionException ex) {
            if (ex.getMessage().contains("item exists")) {
                log.warn("Bloom filter already exists");
            } else {
                log.error("Unable to initialize bloom filter", ex);
                throw ex;
            }
        }
    }

    public boolean addWallet(String walletNumber) {
        Boolean result = redisCommands.dispatch(
                BF_ADD,
                new BooleanOutput<>(UTF8),
                new CommandArgs<>(UTF8)
                        .add(activeFilter)
                        .add(walletNumber)
        );

        boolean added = TRUE.equals(result);
        if (added) {
            log.info("Wallet {} added to bloom filter", walletNumber);
        } else {
            log.info("Wallet {} already exists in bloom filter", walletNumber);
        }

        return added;
    }

    public boolean mightExist(String walletNumber) {

        Boolean result = redisCommands.dispatch(
                BF_EXISTS,
                new BooleanOutput<>(UTF8),
                new CommandArgs<>(UTF8)
                        .add(activeFilter)
                        .add(walletNumber)
        );

        return TRUE.equals(result);
    }

    public void rebuildBloomFilter() {
        log.info("Rebuilding bloom filter...");
        long startedAt = System.currentTimeMillis();

        try {
            // A leftover temp key (e.g. from a crashed run) would make BF.RESERVE fail.
            deleteTempFilter();
            createTempFilter();

            long total = loadWalletsIntoTempFilter();

            // Only reached when the build completed fully.
            swapFilters();

            log.info("Bloom filter rebuilt successfully: {} wallets in {} ms",
                    total, System.currentTimeMillis() - startedAt);
        } catch (Exception e) {
            // Keep serving from the existing active filter; never promote a partial build.
            log.error("Bloom filter rebuild failed; existing active filter kept", e);
            safeDeleteTempFilter();
        }
    }

    // ------------------------------------------------------------------ Redis

    private void createTempFilter() {
        redisCommands.dispatch(
                BF_RESERVE,
                new StatusOutput<>(UTF8),
                new CommandArgs<>(UTF8)
                        .add(tempFilter)
                        .add(BF_FALSE_POSITIVE_RATE)
                        .add(BF_CAPACITY)
        );
    }

    /**
     * Adds many wallets with a single round trip.
     */
    private void addBatchToTempFilter(List<String> wallets) {
        CommandArgs<String, String> args = new CommandArgs<>(UTF8).add(tempFilter);
        wallets.forEach(args::add);

        for (int attempt = 1; true; attempt++) {
            try {
                List<Boolean> result = redisCommands.dispatch(
                        BF_MADD, new BooleanListOutput<>(UTF8), args
                );
                if (result.size() != wallets.size()) {
                    throw new IllegalStateException("Unexpected BF.MADD result size");
                }
                return;
            } catch (RedisException e) {   // includes timeouts and connection errors
                log.warn("BF.MADD failed, attempt {}", attempt, e);
                if (attempt == 3) throw e;
                // optionally back off briefly before retrying
            }
        }
    }

    private void swapFilters() {
        redisCommands.eval(SWAP_SCRIPT, STATUS, activeFilter, tempFilter);
    }

    private void deleteTempFilter() {
        redisCommands.unlink(tempFilter);
    }

    private void safeDeleteTempFilter() {
        try {
            deleteTempFilter();
        } catch (RedisException ex) {
            log.warn("Could not delete temp bloom filter {}", tempFilter, ex);
        }
    }

    // --------------------------------------------------------------------- DB

    /**
     * Streams active wallets from the DB and pushes them to Redis in batches.
     * Any exception propagates so the caller can skip the swap.
     *
     * @return number of wallets added
     */
    private long loadWalletsIntoTempFilter() {
        List<String> batch = new ArrayList<>(BF_BATCH_SIZE);
        long total = 0;
        addBatchToTempFilter(batch);
        total += batch.size();
        return total;
    }
}