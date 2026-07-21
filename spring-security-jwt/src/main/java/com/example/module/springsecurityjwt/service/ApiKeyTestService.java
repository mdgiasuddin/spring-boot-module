package com.example.module.springsecurityjwt.service;

import com.example.module.springsecurityjwt.dto.ApiResponse;
import com.example.module.springsecurityjwt.dto.SecuredRequest;
import com.example.module.springsecurityjwt.util.RequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyTestService {
    private final RequestValidator requestValidator;

    public ApiResponse test(SecuredRequest request) {
        long startTime = System.currentTimeMillis();
        boolean validated = requestValidator.validateRequest(request);
        log.info("Time spent: {}", System.currentTimeMillis() - startTime);
        if (validated) {
            return new ApiResponse("SUCCESS", "Successfully Validated");
        }

        return new ApiResponse("FAILED", "Validation Failed");
    }
}
