package com.example.module.kafkaproducer.controller;

import com.example.module.kafkaproducer.dto.TestEvent;
import com.example.module.kafkaproducer.produer.KafkaProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaProducer kafkaProducer;

    @PostMapping("/send")
    public String send(@Valid @RequestBody TestEvent event) {
        kafkaProducer.sendMessage(event);
        return "Message sent!";
    }
}
