INSERT INTO proforma_rate (
    id,
    proforma_type,
    rate_type,
    range_from,
    range_to,
    price,
    currency,
    active,
    created_at,
    updated_at
)
VALUES (
    gen_random_uuid(),
    'FCL',
    'GIRO_ALIBABA_PERCENT',
    0.000,
    NULL,
    5.00,
    'USD',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);