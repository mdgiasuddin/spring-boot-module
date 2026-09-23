package com.example.module.kafkaconsumer.trace;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaInterceptorConfig {

    private final ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory;

    @PostConstruct
    public void registerRecordInterceptor() {
        kafkaListenerContainerFactory.setRecordInterceptor(new TraceIdConsumerInterceptor());
    }
}
