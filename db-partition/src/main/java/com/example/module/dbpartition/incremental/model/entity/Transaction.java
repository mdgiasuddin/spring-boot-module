package com.example.module.dbpartition.incremental.model.entity;

import com.example.module.dbpartition.common.enumeration.TransactionStatus;
import com.example.module.dbpartition.incremental.model.id.TransactionId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;

import static jakarta.persistence.EnumType.STRING;
import static org.hibernate.type.SqlTypes.NAMED_ENUM;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction implements Persistable<TransactionId> {

    @EmbeddedId
    private TransactionId txnId;

    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;

    @Enumerated(STRING)
    @JdbcTypeCode(NAMED_ENUM)
    private TransactionStatus status;

    private String remarks;

    @Transient
    private boolean newTransaction = true;

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.newTransaction = false;
    }

    @Override
    public TransactionId getId() {
        return txnId;
    }

    @Override
    public boolean isNew() {
        return newTransaction;
    }
}