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
SELECT
    '6a6f6d41-91b4-4eb5-b85b-4a35cc413146'::uuid,
    'GENERAL',
    'CALCULATION_MODE',
    NULL,
    'MODALITY',
    'TEXT',
    'POLICY_2026_09',
    TRUE,
    'Define el método de cálculo vigente para las nuevas proformas: MODALITY utiliza la fórmula original de cada modalidad y LIQUIDATION utiliza la Planilla de Liquidación vigente.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM calculation_parameter
    WHERE scope = 'GENERAL'
      AND code = 'CALCULATION_MODE'
      AND version = 'POLICY_2026_09'
);