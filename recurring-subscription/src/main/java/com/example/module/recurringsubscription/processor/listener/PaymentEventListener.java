package com.example.module.recurringsubscription.processor.listener;

import com.example.module.recurringsubscription.common.dto.event.PaymentEvent;
import com.example.module.recurringsubscription.processor.service.PaymentHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import static com.example.module.recurringsubscription.common.config.KafkaConfig.SUBSCRIPTION_PAYMENTS;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentHandler paymentHandler;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = SUBSCRIPTION_PAYMENTS, groupId = "subscription-payment-processor")
    public void listen(ConsumerRecord<String, byte[]> messageBytes) {
        PaymentEvent event = objectMapper.readValue(messageBytes.value(), PaymentEvent.class);
        log.info("Received message from topic: {}", event);
        paymentHandler.handlePayment(event.paymentId());
    }
}
