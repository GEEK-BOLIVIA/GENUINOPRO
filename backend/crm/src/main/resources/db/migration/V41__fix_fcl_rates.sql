UPDATE proforma_rate
SET price = 3500.00,
    updated_at = CURRENT_TIMESTAMP
WHERE proforma_type = 'FCL'
  AND rate_type = 'ALBO'
  AND active = TRUE;

UPDATE proforma_rate
SET range_to = 24999.990,
    updated_at = CURRENT_TIMESTAMP
WHERE proforma_type = 'FCL'
  AND rate_type = 'COMISION_GENUINO'
  AND range_from = 20000.010;

UPDATE proforma_rate
SET range_from = 25000.000,
    updated_at = CURRENT_TIMESTAMP
WHERE proforma_type = 'FCL'
  AND rate_type = 'COMISION_GENUINO'
  AND price = 10000.00;