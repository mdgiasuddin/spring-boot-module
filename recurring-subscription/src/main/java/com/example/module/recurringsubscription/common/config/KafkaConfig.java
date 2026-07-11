package com.example.module.recurringsubscription.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String SUBSCRIPTION_PAYMENTS = "topic.subscription-payments";

    @Bean
    public NewTopic subscriptionPaymentTopic() {
        return TopicBuilder.name(SUBSCRIPTION_PAYMENTS)
                .partitions(3)
                .build();
    }
}
