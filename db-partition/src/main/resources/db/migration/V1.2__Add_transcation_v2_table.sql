-- 1. Partitioned parent table
CREATE TABLE transactions_uuid
(
    id                  uuid           NOT NULL DEFAULT uuidv7(),
    from_account_number varchar(20)    NOT NULL,
    to_account_number   varchar(20)    NOT NULL,
    amount              numeric(19, 2) NOT NULL,
    status              txn_status     NOT NULL DEFAULT 'PENDING',
    created_at          timestamptz    NOT NULL DEFAULT now(),
    remarks             varchar(255),
    -- the PK must include the partition key
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- 2. Indexes (created on the parent, applied to every partition automatically)
CREATE INDEX idx_txn_uuid_from_account ON transactions_uuid (from_account_number, created_at DESC);
CREATE INDEX idx_txn_uuid_to_account ON transactions_uuid (to_account_number, created_at DESC);

-- 3. Helper to create one daily partition (bounds are explicit UTC days)
CREATE
OR REPLACE FUNCTION create_txn_uuid_partition(p_day date) RETURNS void
LANGUAGE plpgsql AS $$
DECLARE
v_from timestamptz := p_day::timestamp AT TIME ZONE 'UTC';
    v_to
timestamptz := (p_day + 1)::timestamp AT TIME ZONE 'UTC';
    v_name
text        := 'transactions_uuid_' || to_char(p_day, 'YYYY_MM_DD');
BEGIN
EXECUTE format(
        'CREATE TABLE IF NOT EXISTS %I PARTITION OF transactions_uuid
           FOR VALUES FROM (%L) TO (%L) WITH (fillfactor = 90)',
        v_name, v_from, v_to);
END $$;

-- 4. Create partitions for the next 30 days
DO
$$
BEGIN
FOR i IN 0..29 LOOP
        PERFORM create_txn_uuid_partition((now() AT TIME ZONE 'UTC')::date + i);
END LOOP;
END $$;