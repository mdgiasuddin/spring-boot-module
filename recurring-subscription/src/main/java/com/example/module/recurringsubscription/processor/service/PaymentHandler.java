package com.example.module.recurringsubscription.processor.service;

import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.common.repository.jpa.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.module.recurringsubscription.common.enumeration.PaymentStatus.PROCESSING;
import static com.example.module.recurringsubscription.common.enumeration.PaymentStatus.QUEUED;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentHandler {
    private final BankService bankService;
    private final PaymentRepository paymentRepository;

    public void handlePayment(Long paymentId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isEmpty()) {
            log.warn("Payment with id: {} not found", paymentId);
            return;
        }

        Payment payment = paymentOptional.get();
        if (!payment.getStatus().equals(QUEUED)) {
            log.warn("Payment with id: {} is not in QUEUED status", paymentId);
            return;
        }

        payment.setStatus(PROCESSING);
        paymentRepository.save(payment);

        bankService.processPayment(payment);
    }
}
