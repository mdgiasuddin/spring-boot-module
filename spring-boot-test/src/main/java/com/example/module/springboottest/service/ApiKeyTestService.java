package com.example.module.springboottest.service;

import com.example.module.springboottest.dto.ApiResponse;
import com.example.module.springboottest.dto.SampleRequest;
import com.example.module.springboottest.dto.SecuredRequest;
import com.example.module.springboottest.util.SignatureUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyTestService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${api-security.key}")
    private String securityKey;

    public ApiResponse test() {
        SampleRequest request = new SampleRequest(101, "Giash Uddin", LocalDate.of(1995, 10, 2));
        String json = objectMapper.writeValueAsString(request);
        String requestId = UUID.randomUUID().toString().replace("-", "");
        long epochMilli = Instant.now().toEpochMilli();

        String modifiedPayload = String.format("%s|%d|%s", requestId, epochMilli, json);
        String signature = SignatureUtil.sign(securityKey, modifiedPayload);

        return restClient.post()
                .uri("/api/test/api-key")
                .body(new SecuredRequest(requestId, epochMilli, signature, json))
                .retrieve()
                .onStatus(status -> status.value() != 200, (req, res) -> {
                    throw new IllegalArgumentException("Invalid user creation payload provided.");
                })
                .body(ApiResponse.class);
    }
}
