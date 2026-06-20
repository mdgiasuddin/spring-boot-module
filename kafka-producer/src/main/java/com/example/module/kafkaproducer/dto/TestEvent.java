package com.example.module.kafkaproducer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TestEvent(
        @NotBlank
        String subject,
        @NotBlank
        String content,
        @NotNull
        Integer amount
) {
    @Override
    public String toString() {
        return "TestEvent(" +
                "subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ')';
    }
}
