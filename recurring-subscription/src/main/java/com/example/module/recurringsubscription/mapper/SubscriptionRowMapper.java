package com.example.module.recurringsubscription.mapper;

import com.example.module.recurringsubscription.entity.Subscription;
import com.example.module.recurringsubscription.enumeration.Frequency;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubscriptionRowMapper implements RowMapper<Subscription> {

    @Override
    public Subscription mapRow(ResultSet rs, int rowNum) throws SQLException {

        Subscription subscription = new Subscription();

        subscription.setId(rs.getLong("id"));
        subscription.setMerchantName(rs.getString("merchant_name"));
        subscription.setPayerWallet(rs.getString("payer_wallet"));
        subscription.setAmount(rs.getBigDecimal("amount"));
        subscription.setFrequency(Frequency.valueOf(rs.getString("frequency")));
        subscription.setStartDate(rs.getDate("start_date").toLocalDate());
        subscription.setEndDate(rs.getDate("end_date").toLocalDate());
        subscription.setNextPaymentDate(rs.getDate("next_payment_date").toLocalDate());

        Date lastPaymentDate = rs.getDate("last_payment_date");
        subscription.setLastPaymentDate(lastPaymentDate == null ? null : lastPaymentDate.toLocalDate());

        return subscription;
    }
}
