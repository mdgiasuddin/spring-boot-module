package com.example.springneo4j.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @NotBlank
        String name,
        @NotBlank
        String author
) {
}
