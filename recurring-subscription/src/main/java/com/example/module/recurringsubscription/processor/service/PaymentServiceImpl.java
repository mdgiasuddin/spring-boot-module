package com.example.module.recurringsubscription.processor.service;

import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.processor.ratelimiter.RateLimitResult;
import com.example.module.recurringsubscription.processor.ratelimiter.RedisRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements RateLimitedPaymentService {

    private final BankService bankService;
    private final RedisRateLimiter rateLimiter;
    private final Random random = new SecureRandom();
    private static final int JITTER_MS = 5;

    @Override
    public void processPayment(Payment payment, String merchantName) {
        while (true) {
            RateLimitResult result = rateLimiter.acquire();
            if (result.allowed()) {
                bankService.processPayment(payment);
                break;
            }

            try {
                Thread.sleep(result.waitTimeMs() + random.nextInt(JITTER_MS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
