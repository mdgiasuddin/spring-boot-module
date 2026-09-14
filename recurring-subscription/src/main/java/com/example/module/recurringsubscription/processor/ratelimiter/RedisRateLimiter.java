package com.example.module.recurringsubscription.processor.ratelimiter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RedisRateLimiter {

    private static final String KEY = "bank_rate_limiter";
    private static final long MAX_TOKENS = 200;
    private static final long REFILL_RATE = 200;
    private static final long REFILL_WINDOW = 60;
    private static final long INITIAL_TOKENS = 0;

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> script;

    public long acquire() {
        return redisTemplate.execute(
                script,
                Collections.singletonList(KEY),
                String.valueOf(MAX_TOKENS),
                String.valueOf(REFILL_RATE),
                String.valueOf(REFILL_WINDOW),
                String.valueOf(INITIAL_TOKENS)
        );
    }

}
