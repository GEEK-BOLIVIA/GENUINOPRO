ALTER TABLE typed_proforma_calculation_snapshot
    ADD COLUMN IF NOT EXISTS calculation_mode VARCHAR(20);

ALTER TABLE typed_proforma_calculation_snapshot
    ADD COLUMN IF NOT EXISTS calculation_policy_version VARCHAR(50);

ALTER TABLE typed_proforma_calculation_snapshot
    ADD COLUMN IF NOT EXISTS parameter_snapshot_json TEXT;