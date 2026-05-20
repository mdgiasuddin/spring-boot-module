package com.example.module.rabbitmqconsumer.dto;

import jakarta.validation.constraints.NotBlank;

public record TestEvent(
        @NotBlank
        String subject,
        @NotBlank
        String content
) {
    @Override
    public String toString() {
        return "TestEvent(" +
                "subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ')';
    }
}
