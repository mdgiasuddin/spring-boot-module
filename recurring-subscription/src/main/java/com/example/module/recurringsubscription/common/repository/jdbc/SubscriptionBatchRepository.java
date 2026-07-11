package com.example.module.recurringsubscription.common.repository.jdbc;

import com.example.module.recurringsubscription.common.entity.Subscription;
import com.example.module.recurringsubscription.common.mapper.SubscriptionRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubscriptionBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Subscription> fetchSubscriptions(LocalDate date, int limit) {

        String sql = """
                select
                    id,
                    merchant_name,
                    payer_wallet,
                    amount,
                    frequency,
                    start_date,
                    end_date,
                    next_payment_date,
                    last_payment_date
                from subscription
                where next_payment_date = ?
                order by id
                limit ?
                for update skip locked
                """;

        return jdbcTemplate.query(
                sql,
                new SubscriptionRowMapper(),
                Date.valueOf(date),
                limit
        );
    }

    public void batchUpdate(List<Subscription> subscriptions) {
        String sql = """
                update subscription
                set next_payment_date = ?
                where id = ?
                """;

        jdbcTemplate.batchUpdate(
                sql,
                subscriptions,
                1000,
                (ps, subscription) -> {
                    ps.setDate(1, Date.valueOf(subscription.getNextPaymentDate()));
                    ps.setLong(2, subscription.getId());
                });
    }
}
