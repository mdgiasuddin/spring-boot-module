package com.example.module.kafkaconsumer.listener;

import com.example.module.kafkaconsumer.dto.TestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.kafka.support.KafkaHeaders.OFFSET;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class DltListener {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "test-topic-dlt-1", groupId = "dlt-group-1")
    public void listen1(
            ConsumerRecord<String, byte[]> messageBytes,
            @Header(RECEIVED_TOPIC) String topic,
            @Header(RECEIVED_PARTITION) int partition,
            @Header(OFFSET) long offset
    ) {
        TestEvent event = objectMapper.readValue(messageBytes.value(), TestEvent.class);
        log.info("Received message from topic: {}, partition: {}, offset: {}, message: {}",
                topic, partition, offset, event);
    }

    @KafkaListener(topics = "test-topic-dlt-2", groupId = "dlt-group-2")
    public void listen2(
            ConsumerRecord<String, byte[]> messageBytes,
            @Header(RECEIVED_TOPIC) String topic,
            @Header(RECEIVED_PARTITION) int partition,
            @Header(OFFSET) long offset
    ) {
        TestEvent event = objectMapper.readValue(messageBytes.value(), TestEvent.class);
        log.info("Received message from topic: {}, partition: {}, offset: {}, message: {}",
                topic, partition, offset, event);
    }
}
