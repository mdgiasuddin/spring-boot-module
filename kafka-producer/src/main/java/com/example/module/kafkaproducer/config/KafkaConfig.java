package com.example.module.kafkaproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TEST_TOPIC_1 = "test-topic-1";
    public static final String TEST_TOPIC_2 = "test-topic-2";
    public static final String DLT_TOPIC_1 = "test-topic-dlt-1";
    public static final String DLT_TOPIC_2 = "test-topic-dlt-2";

    @Bean
    public NewTopic kafkaTopic1() {
        return TopicBuilder.name(TEST_TOPIC_1)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic kafkaTopic2() {
        return TopicBuilder.name(TEST_TOPIC_2)
                .partitions(2)
                .build();
    }

    @Bean
    public NewTopic kafkaTopicDlt1() {
        return TopicBuilder.name(DLT_TOPIC_1)
                .partitions(2)
                .build();
    }

    @Bean
    public NewTopic kafkaTopicDlt2() {
        return TopicBuilder.name(DLT_TOPIC_2)
                .partitions(2)
                .build();
    }
}
