package com.example.module.rabbitmqproducer.producer;

import com.example.module.rabbitmqproducer.dto.TestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.example.module.rabbitmqproducer.config.RabbitConfig.DIRECT_EXCHANGE;
import static com.example.module.rabbitmqproducer.config.RabbitConfig.DIRECT_ROUTING_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendDirectMessage(TestEvent event) {
        rabbitTemplate.convertAndSend(DIRECT_EXCHANGE,
                DIRECT_ROUTING_KEY,
                event);
        log.info("RabbitMQ sent message: {}", event);
    }
}
