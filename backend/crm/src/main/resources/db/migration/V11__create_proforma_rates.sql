CREATE TABLE proforma_rate (
    id UUID PRIMARY KEY,
    proforma_type VARCHAR(30) NOT NULL,
    rate_type VARCHAR(30) NOT NULL,
    range_from NUMERIC(14, 3) NOT NULL,
    range_to NUMERIC(14, 3),
    price NUMERIC(14, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

INSERT INTO proforma_rate (
    id, proforma_type, rate_type, range_from, range_to, price, currency, active
) VALUES
(gen_random_uuid(), 'LCL', 'CBM', 0.10, 0.99, 250.00, 'USD', TRUE),
(gen_random_uuid(), 'LCL', 'CBM', 1.00, 5.00, 220.00, 'USD', TRUE),
(gen_random_uuid(), 'LCL', 'CBM', 5.01, 10.00, 210.00, 'USD', TRUE),
(gen_random_uuid(), 'LCL', 'CBM', 10.01, 20.00, 200.00, 'USD', TRUE),
(gen_random_uuid(), 'LCL', 'CBM', 20.01, NULL, 190.00, 'USD', TRUE),
(gen_random_uuid(), 'LCL', 'TON', 1.00, NULL, 450.00, 'USD', TRUE);