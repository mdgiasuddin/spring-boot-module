package com.example.module.dbpartition.uuid.model.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionV2UpdateRequest(
        @NotNull
        UUID id,
        @NotNull
        BigDecimal amount
) {
}