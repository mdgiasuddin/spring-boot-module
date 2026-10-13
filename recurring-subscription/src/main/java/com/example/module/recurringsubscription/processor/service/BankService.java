package com.example.module.recurringsubscription.processor.service;

import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.common.enumeration.PaymentStatus;
import com.example.module.recurringsubscription.common.repository.jpa.PaymentRepository;
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
    private final Random random = new SecureRandom();

    public void processPayment(Payment payment) {
        int randNum = random.nextInt(100);
        PaymentStatus status = randNum < 10 ? FAILED : SUCCESS;
        payment.setStatus(status);
        paymentRepository.save(payment);
    }
}
