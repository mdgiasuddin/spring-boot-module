package com.example.module.recurringsubscription.processor.service;

import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.processor.ratelimiter.RateLimitResult;
import com.example.module.recurringsubscription.processor.ratelimiter.RedisRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class GlobalLimitPayServiceImpl implements RateLimitPayService {

    private final BankService bankService;
    private final RedisRateLimiter rateLimiter;

    private final ReentrantLock globalLock = new ReentrantLock(true);   // fair = FIFO

    @Override
    public void processPayment(Payment payment, String merchantId) {
        try {
            acquirePermit();                          // lock held only while waiting for a token
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        bankService.processPayment(payment);      // outside the lock
    }

    private void acquirePermit() throws InterruptedException {
        globalLock.lockInterruptibly();
        try {
            while (true) {
                RateLimitResult result = rateLimiter.acquire();
                if (result.allowed()) {
                    return;
                }
                Thread.sleep(result.waitTimeMs());
            }
        } finally {
            globalLock.unlock();
        }
    }
}
