package com.example.module.kafkaconsumer.listener;

import com.example.module.kafkaconsumer.dto.TestEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class KafkaLister {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(ConsumerRecord<String, byte[]> messageBytes) {
        TestEvent event = objectMapper.readValue(messageBytes.value(), TestEvent.class);
        System.out.println("Received message: " + event);
    }
}
