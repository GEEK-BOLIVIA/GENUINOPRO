ALTER TABLE typed_fcl_proforma
    ADD COLUMN IF NOT EXISTS customer_address VARCHAR(500);

ALTER TABLE typed_fcl_proforma
    ADD COLUMN IF NOT EXISTS tax_exchange_rate NUMERIC(19,4);

ALTER TABLE typed_fcl_proforma
    ADD COLUMN IF NOT EXISTS miscellaneous_expenses_bob NUMERIC(19,2);

ALTER TABLE typed_fcl_proforma
    ADD COLUMN IF NOT EXISTS container_release_usd NUMERIC(19,2);

ALTER TABLE typed_fcl_proforma
    ADD COLUMN IF NOT EXISTS calculation_rule_version VARCHAR(50);