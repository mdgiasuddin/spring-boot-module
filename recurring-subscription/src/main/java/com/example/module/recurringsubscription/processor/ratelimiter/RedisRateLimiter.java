package com.example.module.recurringsubscription.processor.ratelimiter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RedisRateLimiter {

    private static final String KEY = "bank_pay_limiter";
    private static final long REFILL_RATE = 50;
    private static final double BURST_SECONDS = 0.15;
    private static final long REFILL_WINDOW = 1;
    private static final long INITIAL_TOKENS = 0;

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<List<Long>> globalRateLimitScript;
    private final RedisScript<List<Long>> merchantRateLimitScript;

    public RedisRateLimiter(
            StringRedisTemplate redisTemplate,
            @Qualifier("globalRateLimitScript")
            RedisScript<List<Long>> globalRateLimitScript,
            @Qualifier("merchantRateLimitScript")
            RedisScript<List<Long>> merchantRateLimitScript
    ) {
        this.redisTemplate = redisTemplate;
        this.globalRateLimitScript = globalRateLimitScript;
        this.merchantRateLimitScript = merchantRateLimitScript;
    }

    public RateLimitResult acquire() {
        List<Long> result = redisTemplate.execute(
                globalRateLimitScript,
                Collections.singletonList(KEY),
                String.valueOf((long) Math.ceil(BURST_SECONDS * REFILL_RATE)),
                String.valueOf(REFILL_RATE),
                String.valueOf(REFILL_WINDOW),
                String.valueOf(INITIAL_TOKENS)
        );

        return new RateLimitResult(
                result.getFirst() == 1L,
                result.get(1)
        );
    }

    public RateLimitResult acquire(String merchantId, int merchantRps) {
        List<Long> result = redisTemplate.execute(
                merchantRateLimitScript,
                List.of("{bank_pay}:merchant_limiter:" + merchantId, "{bank_pay}:global_limiter"),   // same hash tag for Cluster
                String.valueOf((long) Math.ceil(BURST_SECONDS * merchantRps)), String.valueOf(merchantRps), String.valueOf(REFILL_WINDOW),
                String.valueOf((long) Math.ceil(BURST_SECONDS * REFILL_RATE)), String.valueOf(REFILL_RATE), String.valueOf(REFILL_WINDOW));

        return new RateLimitResult(
                result.getFirst() == 1L,
                result.get(1)
        );
    }

}
