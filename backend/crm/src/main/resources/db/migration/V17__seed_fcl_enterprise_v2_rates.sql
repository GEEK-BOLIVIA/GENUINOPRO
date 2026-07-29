insert into proforma_rate (
    id, proforma_type, rate_type, range_from, range_to, price, currency, active, created_at
) values
-- Comisión Genuino por rango FOB USD
(gen_random_uuid(), 'FCL', 'COMISION_GENUINO', 0, 10000, 6000, 'BOB', true, now()),
(gen_random_uuid(), 'FCL', 'COMISION_GENUINO', 10000.01, 15000, 7000, 'BOB', true, now()),
(gen_random_uuid(), 'FCL', 'COMISION_GENUINO', 15000.01, 20000, 8000, 'BOB', true, now()),
(gen_random_uuid(), 'FCL', 'COMISION_GENUINO', 20000.01, 25000, 9000, 'BOB', true, now()),
(gen_random_uuid(), 'FCL', 'COMISION_GENUINO', 25000.01, 50000, 10000, 'BOB', true, now()),
(gen_random_uuid(), 'FCL', 'COMISION_GENUINO', 50000.01, null, 15000, 'BOB', true, now()),

-- Comisión Giro Chile por rango FOB USD
(gen_random_uuid(), 'FCL', 'COMISION_GIRO_CHILE', 0, 10000, 550, 'USD', true, now()),
(gen_random_uuid(), 'FCL', 'COMISION_GIRO_CHILE', 10000.01, 15000, 600, 'USD', true, now()),
(gen_random_uuid(), 'FCL', 'COMISION_GIRO_CHILE', 15000.01, 20000, 950, 'USD', true, now()),
(gen_random_uuid(), 'FCL', 'COMISION_GIRO_CHILE', 20000.01, 25000, 950, 'USD', true, now()),
(gen_random_uuid(), 'FCL', 'COMISION_GIRO_CHILE', 25000.01, null, 1550, 'USD', true, now()),

-- Costos operativos FCL
(gen_random_uuid(), 'FCL', 'DESPACHANTE', 0, null, 2500, 'BOB', true, now()),
(gen_random_uuid(), 'FCL', 'GASTOS_EXTRA_NIT', 0, null, 2100, 'BOB', true, now());