-- =========================================================
-- ENTERPRISE PROFORMA RATES V1
-- LCL / FCL / HBL / AEREO
-- =========================================================

-- FCL
insert into proforma_rate (
    id, proforma_type, rate_type, range_from, range_to, price, currency, active, created_at
) values
(gen_random_uuid(), 'FCL', 'FCL20', 0, null, 0, 'USD', true, now()),
(gen_random_uuid(), 'FCL', 'FCL40', 0, null, 0, 'USD', true, now()),
(gen_random_uuid(), 'FCL', 'FCL40HQ', 0, null, 0, 'USD', true, now()),
(gen_random_uuid(), 'FCL', 'ALBO', 0, null, 0, 'BOB', true, now()),
(gen_random_uuid(), 'FCL', 'ADA', 0, null, 0, 'BOB', true, now()),
(gen_random_uuid(), 'FCL', 'COMISION', 0, null, 0, 'USD', true, now());

-- HBL
insert into proforma_rate (
    id, proforma_type, rate_type, range_from, range_to, price, currency, active, created_at
) values
(gen_random_uuid(), 'HBL', 'EMISION_HBL', 0, null, 0, 'USD', true, now()),
(gen_random_uuid(), 'HBL', 'HANDLING', 0, null, 0, 'USD', true, now()),
(gen_random_uuid(), 'HBL', 'DOCUMENTACION', 0, null, 0, 'USD', true, now());

-- AEREO
insert into proforma_rate (
    id, proforma_type, rate_type, range_from, range_to, price, currency, active, created_at
) values
(gen_random_uuid(), 'AEREO', 'PESO_REAL', 0, null, 0, 'USD', true, now()),
(gen_random_uuid(), 'AEREO', 'PESO_VOLUMETRICO', 0, null, 0, 'USD', true, now()),
(gen_random_uuid(), 'AEREO', 'AWB', 0, null, 0, 'USD', true, now()),
(gen_random_uuid(), 'AEREO', 'HANDLING', 0, null, 0, 'USD', true, now());