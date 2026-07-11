package com.example.module.recurringsubscription.repository;

import com.example.module.recurringsubscription.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
            insert into payment
            (
                subscription_id,
                execution_date,
                amount,
                status,
                published
            )
            values (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            update payment
            set published = ?
            where id = ?
            """;

    public void batchInsert(List<Payment> payments) {
        jdbcTemplate.batchUpdate(
                INSERT_SQL,
                payments,
                1000,
                (ps, payment) -> {
                    ps.setLong(1, payment.getSubscription().getId());
                    ps.setDate(2, Date.valueOf(payment.getExecutionDate()));
                    ps.setBigDecimal(3, payment.getAmount());
                    ps.setString(4, payment.getStatus().name());
                    ps.setBoolean(5, payment.isPublished());
                });
    }

    public void batchUpdate(List<Payment> payments) {
        jdbcTemplate.batchUpdate(
                UPDATE_SQL,
                payments,
                1000,
                (ps, payment) -> {
                    ps.setBoolean(1, payment.isPublished());
                    ps.setLong(2, payment.getId());
                });
    }
}