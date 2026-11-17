package com.example.module.dbpartition.uuid.controller;

import com.example.module.dbpartition.uuid.model.dto.request.TransactionV2CreateRequest;
import com.example.module.dbpartition.uuid.model.dto.response.TransactionV2Response;
import com.example.module.dbpartition.uuid.service.TransactionV2Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v2/transactions")
@RequiredArgsConstructor
public class TransactionV2Controller {

    private final TransactionV2Service transactionV2Service;

    @PostMapping
    public TransactionV2Response makeTransaction(@Valid @RequestBody TransactionV2CreateRequest request) {
        return transactionV2Service.makeTransaction(request);
    }

    @GetMapping("/{id}")
    public TransactionV2Response getTransaction(
            @PathVariable UUID id
    ) {
        return transactionV2Service.getTransaction(id);
    }
}
