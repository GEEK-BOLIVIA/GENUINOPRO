ALTER TABLE typed_proforma_lcl
    ADD COLUMN IF NOT EXISTS exchange_rate NUMERIC(14,4),
    ADD COLUMN IF NOT EXISTS tax_exchange_rate NUMERIC(14,4),
    ADD COLUMN IF NOT EXISTS calculation_rule_version VARCHAR(50);