package com.example.module.sqsproducer.dto;

import jakarta.validation.constraints.NotBlank;

public record SqsEvent(
        @NotBlank
        String message
) {
}