package com.example.module.rabbitmqconsumer.listener;

import com.example.module.rabbitmqconsumer.dto.TestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitConsumer {

    @RabbitListener(queues = "direct.queue", concurrency = "5")
    public void listen(TestEvent event) {
        log.info("Received message: {}", event);
    }
}
