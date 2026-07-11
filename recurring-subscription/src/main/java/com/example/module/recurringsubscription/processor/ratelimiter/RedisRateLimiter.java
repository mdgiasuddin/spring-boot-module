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
    private static final long MAX_TOKENS = 50;
    private static final long REFILL_RATE = 50;

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> script;

    public long acquire() {
        return redisTemplate.execute(
                script,
                Collections.singletonList(KEY),
                String.valueOf(MAX_TOKENS),
                String.valueOf(REFILL_RATE));
    }

}
