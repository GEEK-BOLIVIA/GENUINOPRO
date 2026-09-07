INSERT INTO calculation_parameter (
    id,
    scope,
    code,
    numeric_value,
    text_value,
    unit,
    version,
    active,
    description,
    created_at,
    updated_at
)
VALUES (
    gen_random_uuid(),
    'LCL',
    'CUSTOMS_FREIGHT_USD_PER_CBM',
    190.000000,
    NULL,
    'USD',
    'LIQ_2026_09',
    TRUE,
    'Tarifa por CBM utilizada para calcular el flete para efectos aduaneros en LCL.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (scope, code, version) DO NOTHING;

UPDATE calculation_parameter
SET active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE version = 'LIQ_2026_09'
  AND (
        (scope = 'GENERAL' AND code = 'CUSTOMS_FREIGHT_PERCENT')
        OR
        (scope = 'LCL' AND code = 'CUSTOMS_INSURANCE_PERCENT')
      );