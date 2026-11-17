package com.example.module.dbpartition.incremental.controller;

import com.example.module.dbpartition.incremental.model.dto.request.TransactionCreateRequest;
import com.example.module.dbpartition.incremental.model.dto.response.TransactionResponse;
import com.example.module.dbpartition.incremental.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public TransactionResponse makeTransaction(@Valid @RequestBody TransactionCreateRequest request) {
        return transactionService.makeTransaction(request);
    }

    @GetMapping("/{id}/{partitionIdx}")
    public TransactionResponse getTransaction(
            @PathVariable long id,
            @PathVariable Instant partitionIdx
    ) {
        return transactionService.getTransaction(id, partitionIdx);
    }
}
