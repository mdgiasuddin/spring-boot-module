package com.example.module.dbpartition.incremental.model.id;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class TransactionId implements Serializable {
    private Long id;
    private Instant createdAt;

    protected TransactionId() {
    }

    public TransactionId(Long id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransactionId that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt);
    }
}