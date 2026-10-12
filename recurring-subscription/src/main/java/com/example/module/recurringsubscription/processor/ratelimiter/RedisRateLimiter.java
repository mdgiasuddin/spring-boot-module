package com.example.module.recurringsubscription.processor.ratelimiter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisRateLimiter {

    private static final String KEY = "bank_rate_limiter";
    private static final long REFILL_RATE = 50;
    private static final double BURST_SECONDS = 0.15;
    private static final long REFILL_WINDOW = 1;
    private static final long INITIAL_TOKENS = 0;

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<List<Long>> script;

    public RateLimitResult acquire() {
        List<Long> result = redisTemplate.execute(
                script,
                Collections.singletonList(KEY),
                String.valueOf((long) (REFILL_RATE * BURST_SECONDS)),
                String.valueOf(REFILL_RATE),
                String.valueOf(REFILL_WINDOW),
                String.valueOf(INITIAL_TOKENS)
        );

        return new RateLimitResult(
                result.getFirst() == 1L,
                result.get(1)
        );
    }

}
