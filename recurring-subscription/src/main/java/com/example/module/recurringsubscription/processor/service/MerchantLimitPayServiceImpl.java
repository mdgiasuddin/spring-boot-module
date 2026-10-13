package com.example.module.recurringsubscription.processor.service;

import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.processor.ratelimiter.MerchantLockRegistry;
import com.example.module.recurringsubscription.processor.ratelimiter.RateLimitResult;
import com.example.module.recurringsubscription.processor.ratelimiter.RedisRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantLimitPayServiceImpl implements RateLimitPayService {

    private final BankService bankService;
    private final RedisRateLimiter rateLimiter;
    private final MerchantLockRegistry lockRegistry;
    private final Random random = new SecureRandom();
    private static final int JITTER_MS = 5;

    private static final Map<String, Integer> merchantLimits = Map.of(
            "Chorki", 10,
            "Rabbithole", 15,
            "Bongo", 20,
            "Banglalink", 25,
            "GP", 30
    );

    @Override
    public void processPayment(Payment payment, String merchantId) {
        acquirePermit(merchantId, getMerchantLimit(merchantId));
        bankService.processPayment(payment);
    }

    /**
     * Blocks until both the merchant and global buckets grant a token.
     * <p>
     * Threads for the same merchant queue up on a fair lock, so only ONE of them polls Redis
     * at a time; the rest are parked (no CPU, no Redis calls) and are served in FIFO order.
     * The lock covers only this permit wait: the payment API call and the retry backoff
     * happen after it is released, so they are never serialized per merchant.
     */
    private void acquirePermit(String merchantId, int rps) {
        try (MerchantLockRegistry.Lease ignored = lockRegistry.acquire(merchantId)) {
            while (true) {
                RateLimitResult result;
                try {
                    result = rateLimiter.acquire(merchantId, rps);
                } catch (DataAccessException e) {
                    log.error("RateLimiter unavailable, retrying in 1s", e);   // fail closed: never call the API unthrottled
                    Thread.sleep(1_000);
                    continue;
                }
                if (result.allowed()) {
                    return;
                }
                Thread.sleep(result.waitTimeMs() + random.nextInt(JITTER_MS));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private int getMerchantLimit(String merchantId) {
        return merchantLimits.getOrDefault(merchantId, 20);
    }
}
