-- 1. Enum type
CREATE TYPE txn_status AS ENUM ('PENDING', 'SUCCEEDED', 'FAILED');

-- 2. Partitioned parent table
CREATE TABLE transactions
(
    id bigint NOT NULL,
    from_account_number varchar(20)    NOT NULL,
    to_account_number   varchar(20)    NOT NULL,
    amount              numeric(19, 2) NOT NULL,
    status              txn_status     NOT NULL DEFAULT 'PENDING',
    created_at          timestamptz    NOT NULL DEFAULT now(),
    remarks             varchar(255),
    -- the PK must include the partition key
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- 3. Indexes (created on the parent, applied to every partition automatically)
CREATE INDEX idx_txn_from_account ON transactions (from_account_number, created_at DESC);
CREATE INDEX idx_txn_to_account ON transactions (to_account_number, created_at DESC);

-- 4. Helper to create one daily partition (bounds are explicit UTC days)
CREATE
OR REPLACE FUNCTION create_txn_partition(p_day date) RETURNS void
LANGUAGE plpgsql AS $$
DECLARE
v_from timestamptz := p_day::timestamp AT TIME ZONE 'UTC';
    v_to
timestamptz := (p_day + 1)::timestamp AT TIME ZONE 'UTC';
    v_name
text        := 'transactions_' || to_char(p_day, 'YYYY_MM_DD');
BEGIN
EXECUTE format(
        'CREATE TABLE IF NOT EXISTS %I PARTITION OF transactions
           FOR VALUES FROM (%L) TO (%L) WITH (fillfactor = 90)',
        v_name, v_from, v_to);
END $$;

-- 5. Create partitions for the next 30 days
DO
$$
BEGIN
FOR i IN 0..29 LOOP
        PERFORM create_txn_partition((now() AT TIME ZONE 'UTC')::date + i);
END LOOP;
END $$;

-- 6. Create sequence for generating id
CREATE SEQUENCE transactions_id_seq INCREMENT BY 100 CACHE 1;