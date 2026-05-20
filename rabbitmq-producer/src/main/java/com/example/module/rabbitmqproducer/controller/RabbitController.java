package com.example.module.rabbitmqproducer.controller;

import com.example.module.rabbitmqproducer.dto.TestEvent;
import com.example.module.rabbitmqproducer.producer.RabbitProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rabbitmq")
@RequiredArgsConstructor
public class RabbitController {
    private final RabbitProducer rabbitProducer;

    @PostMapping("/send")
    public String sendDirectMessage(@Valid @RequestBody TestEvent event) {
        rabbitProducer.sendDirectMessage(event);
        return "Message sent!";
    }
}
