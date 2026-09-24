package com.example.module.rabbitmqproducer.controller;

import com.example.module.rabbitmqproducer.dto.TestEvent;
import com.example.module.rabbitmqproducer.producer.RabbitProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.module.rabbitmqproducer.trace.TraceConstants.SPAN_ID;
import static com.example.module.rabbitmqproducer.trace.TraceConstants.TRACE_ID;

@RestController
@RequestMapping("/api/rabbitmq")
@RequiredArgsConstructor
public class RabbitController {
    private final RabbitProducer rabbitProducer;

    @PostMapping("/send")
    public String sendDirectMessage(@Valid @RequestBody TestEvent event) {
        MDC.put(TRACE_ID, UUID.randomUUID().toString().replace("-", ""));
        MDC.put(SPAN_ID, UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        rabbitProducer.sendDirectMessage(event);
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
        return "Message sent!";
    }
}
