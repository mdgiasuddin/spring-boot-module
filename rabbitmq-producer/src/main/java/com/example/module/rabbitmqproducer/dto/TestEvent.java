package com.example.module.rabbitmqproducer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TestEvent(
        @NotBlank
        String id,
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
                "id='" + id + '\'' +
                "subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", amount=" + amount +
                ')';
    }
}
