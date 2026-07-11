package com.example.module.recurringsubscription.repository;

import com.example.module.recurringsubscription.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PaymentBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<Payment> payments) {
        String sql = """
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

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.batchUpdate(
                connection -> connection.prepareStatement(sql, new String[]{"id"}),
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                        Payment payment = payments.get(i);
                        ps.setLong(1, payment.getSubscription().getId());
                        ps.setDate(2, Date.valueOf(payment.getExecutionDate()));
                        ps.setBigDecimal(3, payment.getAmount());
                        ps.setString(4, payment.getStatus().name());
                        ps.setBoolean(5, payment.isPublished());
                    }

                    @Override
                    public int getBatchSize() {
                        return payments.size();
                    }
                },
                keyHolder
        );

        List<Map<String, Object>> keyList = keyHolder.getKeyList();
        for (int i = 0; i < payments.size(); i++) {
            Number key = (Number) keyList.get(i).get("id");
            if (key != null) {
                payments.get(i).setId(key.longValue());
            }
        }
    }

    public void batchUpdate(List<Payment> payments) {
        String sql = """
                update payment
                set published = ?
                where id = ?
                """;

        jdbcTemplate.batchUpdate(
                sql,
                payments,
                1000,
                (ps, payment) -> {
                    ps.setBoolean(1, payment.isPublished());
                    ps.setLong(2, payment.getId());
                });
    }
}