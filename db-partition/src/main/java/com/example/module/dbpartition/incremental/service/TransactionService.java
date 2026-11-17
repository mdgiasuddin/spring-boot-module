package com.example.module.dbpartition.incremental.service;

import com.example.module.dbpartition.incremental.model.dto.request.TransactionCreateRequest;
import com.example.module.dbpartition.incremental.model.dto.response.TransactionResponse;
import com.example.module.dbpartition.incremental.model.entity.Transaction;
import com.example.module.dbpartition.incremental.model.id.TransactionId;
import com.example.module.dbpartition.incremental.model.idgenerator.TransactionIdGenerator;
import com.example.module.dbpartition.incremental.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

import static com.example.module.dbpartition.common.enumeration.TransactionStatus.PENDING;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionIdGenerator transactionIdGenerator;
    private final TransactionRepository transactionRepository;

    public TransactionResponse makeTransaction(TransactionCreateRequest request) {
        Transaction transaction = new Transaction();
        transaction.setTxnId(new TransactionId(transactionIdGenerator.nextId(), Instant.now()));
        transaction.setFromAccountNumber(request.fromAccountNumber());
        transaction.setToAccountNumber(request.toAccountNumber());
        transaction.setAmount(request.amount());
        transaction.setStatus(PENDING);
        transaction.setRemarks(request.remarks());

        return new TransactionResponse(transactionRepository.save(transaction));
    }

    public TransactionResponse getTransaction(long id, Instant partitionIdx) {
        Instant cratedAtFrom = partitionIdx.truncatedTo(DAYS);
        Instant cratedAtTo = cratedAtFrom.plus(1, DAYS);

        Optional<Transaction> transaction = transactionRepository.getTransactionById(id, cratedAtFrom, cratedAtTo);
        return transaction.map(TransactionResponse::new).orElse(null);
    }
}
