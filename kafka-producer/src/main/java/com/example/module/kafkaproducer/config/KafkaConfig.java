package com.example.module.kafkaproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TEST_TOPIC = "test-topic";

    @Bean
    public NewTopic kafkaTopic() {
        return TopicBuilder.name(TEST_TOPIC)
                .partitions(3)
                .build();
    }
}
