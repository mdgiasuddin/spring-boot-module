package com.example.module.dbpartition.uuid.model.entity;

import com.example.module.dbpartition.common.enumeration.TransactionStatus;
import com.example.module.dbpartition.uuid.model.id.TransactionIdV2;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;

import static jakarta.persistence.EnumType.STRING;
import static org.hibernate.type.SqlTypes.NAMED_ENUM;

@Entity
@Table(name = "transactions_uuid")
@DynamicUpdate
@Getter
@Setter
public class TransactionV2 implements Persistable<TransactionIdV2> {
    @EmbeddedId
    private TransactionIdV2 txnId;

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
    public TransactionIdV2 getId() {
        return txnId;
    }

    @Override
    public boolean isNew() {
        return newTransaction;
    }
}
