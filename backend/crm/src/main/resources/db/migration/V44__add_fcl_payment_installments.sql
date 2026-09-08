ALTER TABLE typed_proforma_fcl
    ADD COLUMN IF NOT EXISTS payment1_amount_usd NUMERIC(14,2),
    ADD COLUMN IF NOT EXISTS payment1_method VARCHAR(30),

    ADD COLUMN IF NOT EXISTS payment2_amount_usd NUMERIC(14,2),
    ADD COLUMN IF NOT EXISTS payment2_method VARCHAR(30),

    ADD COLUMN IF NOT EXISTS payment3_amount_usd NUMERIC(14,2),
    ADD COLUMN IF NOT EXISTS payment3_method VARCHAR(30),

    ADD COLUMN IF NOT EXISTS payment4_amount_usd NUMERIC(14,2),
    ADD COLUMN IF NOT EXISTS payment4_method VARCHAR(30);