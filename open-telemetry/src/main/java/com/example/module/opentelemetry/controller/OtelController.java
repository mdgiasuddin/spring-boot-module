package com.example.module.opentelemetry.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/otel")
public class OtelController {

    @GetMapping
    public String otel() {
        log.info("otel");
        return "otel";
    }
}
