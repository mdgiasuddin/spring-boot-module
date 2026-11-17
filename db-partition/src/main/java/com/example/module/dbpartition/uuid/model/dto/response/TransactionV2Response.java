package com.example.module.dbpartition.uuid.model.dto.response;

import com.example.module.dbpartition.common.enumeration.TransactionStatus;
import com.example.module.dbpartition.uuid.model.entity.TransactionV2;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

public record TransactionV2Response(
        UUID id,
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        String remarks,
        TransactionStatus status,
        @JsonFormat(shape = STRING)
        Instant createdAt
) {
    public TransactionV2Response(TransactionV2 transaction) {
        this(
                transaction.getTxnId().getId(),
                transaction.getFromAccountNumber(),
                transaction.getToAccountNumber(),
                transaction.getAmount(),
                transaction.getRemarks(),
                transaction.getStatus(),
                transaction.getTxnId().getCreatedAt()
        );
    }
}
