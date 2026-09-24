package com.example.module.sqsproducer.controller;

import com.example.module.sqsproducer.dto.SqsEvent;
import com.example.module.sqsproducer.service.SqsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.example.module.sqsproducer.trace.TraceConstants.SPAN_ID;
import static com.example.module.sqsproducer.trace.TraceConstants.TRACE_ID;

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
        MDC.put(TRACE_ID, UUID.randomUUID().toString().replace("-", ""));
        MDC.put(SPAN_ID, UUID.randomUUID().toString().replace("-", "").substring(0, 8));

        sqsService.sendMessage(myQueue, objectMapper.writeValueAsString(event));

        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);

        return "Message sent to SQS!";
    }
}
