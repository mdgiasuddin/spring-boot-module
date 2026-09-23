package com.example.module.kafkaconsumer.listener;

import com.example.module.kafkaconsumer.dto.TestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.kafka.support.KafkaHeaders.OFFSET;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListener {
    private final ObjectMapper objectMapper;

    @org.springframework.kafka.annotation.KafkaListener(topics = "test-topic-1", groupId = "test-group-1")
    public void listen1(
            ConsumerRecord<String, byte[]> messageBytes,
            @Header(RECEIVED_TOPIC) String topic,
            @Header(RECEIVED_PARTITION) String partition,
            @Header(OFFSET) long offset
    ) {
        log.info("Received message from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        TestEvent event = objectMapper.readValue(messageBytes.value(), TestEvent.class);
        if (event.amount() > 100) {
            log.error("Amount is too high");
            throw new RuntimeException("Amount is too high");
        }
        log.info("Processing message from topic: {}, partition: {}, offset: {}, message: {}",
                topic, partition, offset, event);
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "test-topic-2", groupId = "test-group-2")
    public void listen2(
            ConsumerRecord<String, byte[]> messageBytes,
            @Header(RECEIVED_TOPIC) String topic,
            @Header(RECEIVED_PARTITION) String partition,
            @Header(OFFSET) long offset
    ) {
        TestEvent event = objectMapper.readValue(messageBytes.value(), TestEvent.class);
        if (event.amount() > 100) {
            throw new RuntimeException("Amount is too high");
        }
        log.info("Received message from topic: {}, partition: {}, offset: {}, message: {}",
                topic, partition, offset, event);
    }
}
