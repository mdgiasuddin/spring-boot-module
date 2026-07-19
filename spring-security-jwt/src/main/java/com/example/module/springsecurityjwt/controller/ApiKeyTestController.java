package com.example.module.springsecurityjwt.controller;

import com.example.module.springsecurityjwt.dto.ApiResponse;
import com.example.module.springsecurityjwt.dto.SecuredRequest;
import com.example.module.springsecurityjwt.service.ApiKeyTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/api-key")
@RequiredArgsConstructor
public class ApiKeyTestController {

    private final ApiKeyTestService apiKeyTestService;

    @PostMapping
    public ApiResponse test(@Valid @RequestBody SecuredRequest request) {
        return apiKeyTestService.test(request);
    }
}
