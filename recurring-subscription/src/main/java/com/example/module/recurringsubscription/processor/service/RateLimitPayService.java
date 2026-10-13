package com.example.module.recurringsubscription.processor.service;

import com.example.module.recurringsubscription.common.entity.Payment;

public interface RateLimitPayService {
    void processPayment(Payment payment, String merchantId);
}