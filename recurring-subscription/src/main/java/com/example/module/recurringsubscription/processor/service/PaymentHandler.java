package com.example.module.recurringsubscription.processor.service;

import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.common.repository.jpa.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.module.recurringsubscription.common.enumeration.PaymentStatus.PROCESSING;
import static com.example.module.recurringsubscription.common.enumeration.PaymentStatus.QUEUED;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentHandler {
    private final BankService bankService;
    private final PaymentRepository paymentRepository;

    public void handlePayment(Long paymentId) {
        int claimed = paymentRepository.claim(paymentId, QUEUED, PROCESSING);

        if (claimed == 0) {
            log.warn("Payment with id: {} not found or already claimed", paymentId);
            return;
        }

        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        bankService.processPayment(payment);
    }
}
