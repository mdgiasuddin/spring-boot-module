package com.example.module.sqsproducer.controller;

import com.example.module.sqsproducer.dto.SqsEvent;
import com.example.module.sqsproducer.service.SqsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/sqs")
@RequiredArgsConstructor
public class SqsController {
    private final SqsService sqsService;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queues.my-queue}")
    private String myQueue;

    @PostMapping("/send")
    public String send(@Valid @RequestBody SqsEvent event) {
        sqsService.sendMessage(myQueue, objectMapper.writeValueAsString(event));
        return "Message sent to SQS!";
    }
}
