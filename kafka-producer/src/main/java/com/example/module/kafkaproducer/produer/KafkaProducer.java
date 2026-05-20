package com.example.module.kafkaproducer.produer;

import com.example.module.kafkaproducer.dto.TestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static com.example.module.kafkaproducer.config.KafkaConfig.TEST_TOPIC;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, TestEvent> kafkaTemplate;

    public void sendMessage(TestEvent event) {
        Message<TestEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(TOPIC, TEST_TOPIC)
                .build();

        kafkaTemplate.send(message);
        log.info("Message sent -> {}", event);
    }
}
