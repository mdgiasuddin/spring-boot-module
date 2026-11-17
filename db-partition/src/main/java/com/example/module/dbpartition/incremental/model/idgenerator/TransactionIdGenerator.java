package com.example.module.dbpartition.incremental.model.idgenerator;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionIdGenerator {

    private static final int BLOCK_SIZE = 100;   // must equal the sequence INCREMENT

    private final JdbcTemplate jdbc;
    private long next = 0;
    private long limit = 0;

    public TransactionIdGenerator(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void validateIncrement() {
        Long inc = jdbc.queryForObject(
                "select increment_by from pg_sequences where sequencename = 'transactions_id_seq'",
                Long.class
        );
        if (inc == null || inc != BLOCK_SIZE) {
            throw new IllegalStateException(
                    "transactions_id_seq increment (" + inc + ") != BLOCK_SIZE (" + BLOCK_SIZE + ")"
            );
        }
    }

    public synchronized long nextId() {
        if (next >= limit) {
            Long start = jdbc.queryForObject("select nextval('transactions_id_seq')", Long.class);
            if (start == null) throw new IllegalStateException("nextval returned null");
            next = start;
            limit = start + BLOCK_SIZE;
        }
        return next++;
    }
}
