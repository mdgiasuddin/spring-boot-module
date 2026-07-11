package com.example.module.recurringsubscription.common.dto.event;


import jakarta.annotation.Nonnull;

public record PaymentEvent(
        Long paymentId
) {

    @Override
    @Nonnull
    public String toString() {
        return "PaymentEvent(" +
                "paymentId=" + paymentId +
                ')';
    }
}
