package com.example.module.recurringsubscription.messaging.producer;

import com.example.module.recurringsubscription.dto.event.PaymentEvent;
import com.example.module.recurringsubscription.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.module.recurringsubscription.messaging.config.KafkaConfig.SUBSCRIPTION_PAYMENTS;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void sendPaymentEvent(List<Payment> payments) {
        payments.forEach(payment -> {
            Message<PaymentEvent> message = MessageBuilder
                    .withPayload(new PaymentEvent(payment.getId()))
                    .setHeader(TOPIC, SUBSCRIPTION_PAYMENTS)
                    .build();

            kafkaTemplate.send(message);
            log.info("Message sent -> {}", payment.getId());
        });

    }
}
