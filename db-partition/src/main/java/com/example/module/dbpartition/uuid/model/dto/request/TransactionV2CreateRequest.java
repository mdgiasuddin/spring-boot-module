package com.example.module.dbpartition.uuid.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionV2CreateRequest(
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