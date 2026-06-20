package com.example.module.kafkaconsumer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (consumerRecord, exception) -> {
                    String baseTopic = consumerRecord.topic();
                    log.info("Recovering record from topic: {}, partition: {}, offset: {}", baseTopic, consumerRecord.partition(), consumerRecord.offset());
                    String dltTopic = switch (baseTopic) {
                        case "test-topic-1" -> "test-topic-dlt-1";
                        case "test-topic-2" -> "test-topic-dlt-2";
                        default -> "test-topic-dlt-1"; // Route all others to a known DLT
                    };

                    TopicPartition tp = new TopicPartition(dltTopic, -1);
                    log.info("Routing to DLT: {}", tp);
                    return tp;
                }
        );

        return new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 2L));
    }
}
