-- ============================================================
-- V33
-- Parámetros iniciales de Planilla de Liquidación
-- y normalización del nombre de comisión de transferencia.
-- ============================================================

-- 1. Parámetros generales confirmados en las planillas.
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
    'IVA_PERCENT',
    0.149400,
    NULL,
    'PERCENT',
    'LIQ_2026_09',
    TRUE,
    'IVA utilizado en la liquidación aduanera.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (scope, code, version) DO NOTHING;


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
    'INSURANCE_PERCENT_DEFAULT',
    0.020000,
    NULL,
    'PERCENT',
    'LIQ_2026_09',
    TRUE,
    'Porcentaje de seguro por defecto cuando corresponde calcularlo sobre el FOB.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (scope, code, version) DO NOTHING;


-- 2. Normalizar el nombre histórico de la comisión.
UPDATE proforma_rate
SET rate_type = 'COMISION_TRANSFERENCIA',
    updated_at = CURRENT_TIMESTAMP
WHERE rate_type = 'COMISION_GIRO_CHILE';


-- 3. No se duplican todavía rangos hacia LCL.
-- COMISION_TRANSFERENCIA ya está habilitada en la UI para LCL,
-- pero sus rangos deben cargarse después de validar la regla
-- definitiva aplicable a LCL.