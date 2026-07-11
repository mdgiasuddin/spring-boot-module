INSERT INTO subscription (merchant_name, payer_wallet, amount, frequency, start_date, end_date, next_payment_date,
                          last_payment_date)
SELECT (ARRAY ['Bongo', 'Chorki', 'Rabbithole', 'GP', 'Banglalink'])[floor(random() * 5 + 1)] AS merchant_name,
       '01' || lpad(floor(random() * 1000000000)::text, 9, '0')                               AS payer_wallet,
       (floor(random() * 46 + 5) * 10)::numeric                                               AS amount,
       (ARRAY ['WEEKLY', 'MONTHLY', 'YEARLY'])[floor(random() * 3 + 1)]                       AS frequency,
       CURRENT_DATE - (random() * 365)::integer                                               AS start_date,
       '2028-01-01'::date + (random() * 365)::integer                                         AS end_date,
       CURRENT_DATE                                                                           AS next_payment_date,
       NULL                                                                                   AS last_payment_date
FROM generate_series(1, 50000);
