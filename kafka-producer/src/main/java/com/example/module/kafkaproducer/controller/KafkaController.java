package com.example.module.kafkaproducer.controller;

import com.example.module.kafkaproducer.dto.TestEvent;
import com.example.module.kafkaproducer.produer.KafkaProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.module.kafkaproducer.trace.TraceConstants.SPAN_ID;
import static com.example.module.kafkaproducer.trace.TraceConstants.TRACE_ID;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaProducer kafkaProducer;

    @PostMapping("/send/{topic}")
    public String send(@Valid @RequestBody TestEvent event, @PathVariable String topic) {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        String spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, spanId);

        kafkaProducer.sendMessage(event, topic);

        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);

        return "Message sent!";
    }
}
