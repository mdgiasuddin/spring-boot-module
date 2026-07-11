package com.example.module.recurringsubscription.messaging.producer;

import com.example.module.recurringsubscription.entity.Payment;
import com.example.module.recurringsubscription.repository.PaymentBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final PaymentBatchRepository paymentBatchRepository;
    private final KafkaTemplate<String, Long> kafkaTemplate;

    public void sendPaymentEvent(List<Payment> payments) {
        payments.forEach(payment -> {
//            kafkaTemplate.send(SUBSCRIPTION_PAYMENTS, payment.getId());
            payment.setPublished(true);
        });

        paymentBatchRepository.batchUpdate(payments);
    }
}
