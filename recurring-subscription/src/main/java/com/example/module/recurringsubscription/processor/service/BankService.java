package com.example.module.recurringsubscription.processor.service;

import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.common.enumeration.PaymentStatus;
import com.example.module.recurringsubscription.common.repository.jpa.PaymentRepository;
import com.example.module.recurringsubscription.processor.ratelimiter.RateLimitResult;
import com.example.module.recurringsubscription.processor.ratelimiter.RedisRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

import static com.example.module.recurringsubscription.common.enumeration.PaymentStatus.FAILED;
import static com.example.module.recurringsubscription.common.enumeration.PaymentStatus.SUCCESS;

@Service
@RequiredArgsConstructor
public class BankService {

    private final PaymentRepository paymentRepository;
    private final RedisRateLimiter rateLimiter;
    private final Random random = new SecureRandom();
    private static final int JITTER_MS = 5;

    public void processPayment(Payment payment) {
        while (true) {
            RateLimitResult result = rateLimiter.acquire();
            if (result.allowed()) {
                int randNum = random.nextInt(100);
                PaymentStatus status = randNum < 10 ? FAILED : SUCCESS;
                payment.setStatus(status);
                paymentRepository.save(payment);
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
