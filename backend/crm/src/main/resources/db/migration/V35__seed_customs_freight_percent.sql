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
    'GENERAL',
    'CUSTOMS_FREIGHT_PERCENT',
    0.100000,
    NULL,
    'PERCENT',
    'LIQ_2026_09',
    TRUE,
    'Porcentaje del transporte real considerado para efectos de la Aduana Nacional.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (scope, code, version) DO NOTHING;