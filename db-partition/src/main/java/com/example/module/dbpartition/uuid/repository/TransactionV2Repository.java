package com.example.module.dbpartition.uuid.repository;

import com.example.module.dbpartition.uuid.model.entity.TransactionV2;
import com.example.module.dbpartition.uuid.model.id.TransactionIdV2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionV2Repository extends JpaRepository<TransactionV2, TransactionIdV2> {

}