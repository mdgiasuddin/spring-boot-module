package com.example.module.dbpartition.uuid.repository;

import com.example.module.dbpartition.uuid.model.entity.TransactionV2;
import com.example.module.dbpartition.uuid.model.id.TransactionIdV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface TransactionV2Repository extends JpaRepository<TransactionV2, TransactionIdV2> {

    @Query("SELECT t FROM TransactionV2 t WHERE t.txnId.id = :id AND t.txnId.createdAt >= :createdAtFrom AND t.txnId.createdAt < :createAtTo")
    Optional<TransactionV2> getTransactionById(UUID id, Instant createdAtFrom, Instant createAtTo);
}