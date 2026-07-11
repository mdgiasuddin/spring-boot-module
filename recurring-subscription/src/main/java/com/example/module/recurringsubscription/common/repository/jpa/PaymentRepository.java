package com.example.module.recurringsubscription.common.repository.jpa;

import com.example.module.recurringsubscription.common.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}