package com.example.module.recurringsubscription.processor.service;

import com.example.module.recurringsubscription.common.entity.Payment;

public interface RateLimitedPaymentService {
    void processPayment(Payment payment, String merchantName);
}