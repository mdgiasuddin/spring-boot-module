package com.example.module.springboottest.service;

import com.example.module.springboottest.dto.SamplePayload;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void testPayload() {
        SamplePayload payload = new SamplePayload(
                "123",
                "Hello, World!",
                "2023-09-20T10:30:00Z"
        );

        String json = objectMapper.writeValueAsString(payload);
        log.info("Serialized payload: {}", json);
        log.info("Deserialized payload: {}", objectMapper.readValue(json, SamplePayload.class));
    }

    public void test() {
        log.info("Test Service");
    }
}
