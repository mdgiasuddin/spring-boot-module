package com.example.module.rabbitmqproducer.controller;

import com.example.module.rabbitmqproducer.dto.TestEvent;
import com.example.module.rabbitmqproducer.producer.RabbitProducer;
import com.example.module.rabbitmqproducer.trace.TraceUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/api/rabbitmq")
@RequiredArgsConstructor
public class RabbitController {
    private final RabbitProducer rabbitProducer;
    private final Random random = new SecureRandom();

    @PostMapping("/send")
    public String sendDirectMessage(@Valid @RequestBody TestEvent event) {
        TraceUtil.setNewTraceId();
        rabbitProducer.sendDirectMessage(event);
        TraceUtil.clearTraceId();
        return "Message sent!";
    }

    @PostMapping("/send/bulk")
    public String sendDirectBulkMessage() {
        TraceUtil.setNewTraceId();
        log.info("Sending bulk messages");
        for (int i = 1; i <= 10; i++) {
            try {
                TraceUtil.setNewTraceId();
                log.info("Sending message {}", i);
                TestEvent event = new TestEvent(String.valueOf(i), "subject-" + i, "content-" + i, random.nextInt(10, 80));
                rabbitProducer.sendDirectMessage(event);
            } finally {
                TraceUtil.clearTraceId();
            }
        }
        TraceUtil.clearTraceId();
        return "Message sent!";
    }
}
