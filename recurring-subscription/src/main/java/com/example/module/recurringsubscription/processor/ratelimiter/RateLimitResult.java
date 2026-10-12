package com.example.module.recurringsubscription.processor.ratelimiter;

public record RateLimitResult(
        boolean allowed,
        long waitTimeMs
) {
}
