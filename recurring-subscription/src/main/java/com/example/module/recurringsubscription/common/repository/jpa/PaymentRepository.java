package com.example.module.recurringsubscription.common.repository.jpa;

import com.example.module.recurringsubscription.common.entity.Payment;
import com.example.module.recurringsubscription.common.enumeration.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Modifying
    @Transactional
    @Query("update Payment p set p.status = :to where p.id = :id and p.status = :from")
    int claim(@Param("id") Long id, @Param("from") PaymentStatus from, @Param("to") PaymentStatus to);
}