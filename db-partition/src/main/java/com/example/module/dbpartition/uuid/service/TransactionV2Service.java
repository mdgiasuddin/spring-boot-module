package com.example.module.dbpartition.uuid.service;

import com.example.module.dbpartition.uuid.model.dto.request.TransactionV2CreateRequest;
import com.example.module.dbpartition.uuid.model.dto.response.TransactionV2Response;
import com.example.module.dbpartition.uuid.model.entity.TransactionV2;
import com.example.module.dbpartition.uuid.model.id.TransactionIdV2;
import com.example.module.dbpartition.uuid.model.idgenerator.UuidV7Generator;
import com.example.module.dbpartition.uuid.repository.TransactionV2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import static com.example.module.dbpartition.common.enumeration.TransactionStatus.PENDING;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class TransactionV2Service {

    private final UuidV7Generator uuidGenerator;
    private final TransactionV2Repository transactionV2Repository;

    public TransactionV2Response makeTransaction(TransactionV2CreateRequest request) {
        TransactionIdV2 txnId = new TransactionIdV2(uuidGenerator.nextId());
        TransactionV2 transaction = new TransactionV2();
        transaction.setTxnId(txnId);
        transaction.setFromAccountNumber(request.fromAccountNumber());
        transaction.setToAccountNumber(request.toAccountNumber());
        transaction.setAmount(request.amount());
        transaction.setStatus(PENDING);
        transaction.setRemarks(request.remarks());

        return new TransactionV2Response(transactionV2Repository.save(transaction));
    }

    public TransactionV2Response getTransaction(UUID id) {
        TransactionIdV2 txnId = new TransactionIdV2(id);
        Instant createdAtFrom = txnId.getCreatedAt().truncatedTo(DAYS);
        Instant createdAtTo = createdAtFrom.plus(1, DAYS);

        return transactionV2Repository.getTransactionById(id, createdAtFrom, createdAtTo)
                .map(TransactionV2Response::new)
                .orElse(null);
    }
}
