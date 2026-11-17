package com.example.module.dbpartition.uuid.model.id;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class TransactionIdV2 implements Serializable {
    private UUID id;
    private Instant createdAt;

    protected TransactionIdV2() {
    }

    private TransactionIdV2(UUID id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    /**
     * New key: the UUIDv7 is generated first and created_at is derived from it.
     */
    public TransactionIdV2(UUID uuidV7) {
        Instant timestamp = Instant.ofEpochMilli(uuidV7.getMostSignificantBits() >>> 16); // millisecond precision
        this(uuidV7, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransactionIdV2 that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt);
    }
}
