package com.example.module.dbpartition.incremental.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionCreateRequest(
        @NotBlank
        String fromAccountNumber,
        @NotBlank
        String toAccountNumber,
        @NotNull
        BigDecimal amount,
        @NotBlank
        String remarks
) {
}