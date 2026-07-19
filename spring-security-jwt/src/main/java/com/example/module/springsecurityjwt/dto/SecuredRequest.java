package com.example.module.springsecurityjwt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SecuredRequest(
        @NotBlank
        String requestId,
        @NotNull
        Long timeStamp,
        @NotBlank
        String signature,
        @NotBlank
        String payload
) {
}
