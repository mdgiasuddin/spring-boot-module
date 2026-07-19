package com.example.module.springboottest.controller;

import com.example.module.springboottest.dto.ApiResponse;
import com.example.module.springboottest.service.ApiKeyTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-key")
@RequiredArgsConstructor
public class ApiKeyTestController {
    private final ApiKeyTestService apiKeyTestService;

    @GetMapping
    public ApiResponse test() {
        return apiKeyTestService.test();
    }

}
