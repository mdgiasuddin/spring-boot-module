package com.example.module.dbpartition.incremental.model.dto.request;

import com.example.module.dbpartition.common.enumeration.TransactionStatus;
import com.example.module.dbpartition.incremental.model.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.time.temporal.ChronoUnit.DAYS;

public record TransactionResponse(
        long id,
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        String remarks,
        TransactionStatus status,
        @JsonFormat(shape = STRING)
        Instant createdAt,
        @JsonFormat(shape = STRING)
        Instant partitionIdx
) {
    public TransactionResponse(Transaction transaction) {
        this(
                transaction.getTxnId().getId(),
                transaction.getFromAccountNumber(),
                transaction.getToAccountNumber(),
                transaction.getAmount(),
                transaction.getRemarks(),
                transaction.getStatus(),
                transaction.getTxnId().getCreatedAt(),
                transaction.getTxnId().getCreatedAt().truncatedTo(DAYS)
        );
    }
}
