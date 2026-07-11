package com.example.module.recurringsubscription.common.entity;

import com.example.module.recurringsubscription.common.enumeration.Frequency;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table
@Getter
@Setter
public class Subscription {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String merchantName;
    private String payerWallet;
    private BigDecimal amount;
    @Enumerated(STRING)
    private Frequency frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextPaymentDate;
    private LocalDate lastPaymentDate;
}
