package com.example.module.dbpartition.incremental.repository;

import com.example.module.dbpartition.incremental.model.entity.Transaction;
import com.example.module.dbpartition.incremental.model.id.TransactionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, TransactionId> {

    @Query("SELECT t FROM Transaction t WHERE t.txnId.id = :id AND t.txnId.createdAt >= :createdAtFrom AND t.txnId.createdAt < :createAtTo")
    Optional<Transaction> getTransactionById(long id, Instant createdAtFrom, Instant createAtTo);
}
