package com.example.module.rabbitmqconsumer.listener;

import com.example.module.rabbitmqconsumer.dto.TestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.example.module.rabbitmqconsumer.config.RabbitConfig.DLQ_QUEUE;
import static com.example.module.rabbitmqconsumer.config.RabbitConfig.MAIN_QUEUE;

@Slf4j
@Service
public class RabbitConsumer {

    @RabbitListener(queues = MAIN_QUEUE, concurrency = "5")
    public void listen(TestEvent event) {
        if (event.amount() > 100) {
            throw new RuntimeException("Amount is too high");
        }
        log.info("Received message from main queue: {}", event);
    }

    @RabbitListener(queues = DLQ_QUEUE, concurrency = "5")
    public void listenDlq(TestEvent event) {
        log.info("Received message from dl queue: {}", event);
    }
}
