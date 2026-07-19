package com.example.module.springsecurityjwt.service;

import com.example.module.springsecurityjwt.dto.ApiResponse;
import com.example.module.springsecurityjwt.dto.SecuredRequest;
import com.example.module.springsecurityjwt.util.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiKeyTestService {
    private final RequestValidator requestValidator;

    public ApiResponse test(SecuredRequest request) {
        boolean validated = requestValidator.validateRequest(request);

        if (validated) {
            return new ApiResponse("SUCCESS", "Successfully Validated");
        }

        return new ApiResponse("FAILED", "Validation Failed");
    }
}
