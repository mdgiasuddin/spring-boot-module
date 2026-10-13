package com.example.module.recurringsubscription.processor.service;

import com.example.module.recurringsubscription.common.dto.event.PaymentEvent;
import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.common.repository.jpa.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.example.module.recurringsubscription.common.enumeration.PaymentStatus.PROCESSING;
import static com.example.module.recurringsubscription.common.enumeration.PaymentStatus.QUEUED;

@Slf4j
@Service
public class PaymentHandler {
    private final RateLimitPayService paymentService;
    private final PaymentRepository paymentRepository;

    public PaymentHandler(
            @Qualifier("merchantLimitPayServiceImpl")
            RateLimitPayService paymentService,
            PaymentRepository paymentRepository
    ) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    public void handlePayment(PaymentEvent event) {
        int claimed = paymentRepository.claim(event.paymentId(), QUEUED, PROCESSING);

        if (claimed == 0) {
            log.warn("Payment with id: {} not found or already claimed", event.paymentId());
            return;
        }

        Payment payment = paymentRepository.findById(event.paymentId()).orElseThrow();
        paymentService.processPayment(payment, event.merchantId());
    }
}
