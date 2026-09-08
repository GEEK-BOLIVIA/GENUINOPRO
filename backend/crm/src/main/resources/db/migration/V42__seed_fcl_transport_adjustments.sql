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
VALUES
(
    gen_random_uuid(),
    'FCL',
    'MARITIME_SELL_MARKUP_USD',
    500.000000,
    NULL,
    'USD',
    'FCL_2026_09',
    TRUE,
    'Margen comercial agregado al transporte marítimo base FCL.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    'FCL',
    'INLAND_SELL_MARKUP_USD',
    200.000000,
    NULL,
    'USD',
    'FCL_2026_09',
    TRUE,
    'Margen comercial agregado al transporte terrestre base FCL.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    'FCL',
    'CUSTOMS_MARITIME_ADJUSTMENT_USD',
    -300.000000,
    NULL,
    'USD',
    'FCL_2026_09',
    TRUE,
    'Ajuste del transporte marítimo base para efectos aduaneros FCL.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    'FCL',
    'CUSTOMS_INLAND_ADJUSTMENT_USD',
    -800.000000,
    NULL,
    'USD',
    'FCL_2026_09',
    TRUE,
    'Ajuste del transporte terrestre base para efectos aduaneros FCL.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (scope, code, version) DO NOTHING;