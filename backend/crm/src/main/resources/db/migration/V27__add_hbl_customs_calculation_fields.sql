ALTER TABLE typed_proforma_hbl
ADD COLUMN IF NOT EXISTS customs_freight_usd NUMERIC(14,2);

ALTER TABLE typed_proforma_hbl
ADD COLUMN IF NOT EXISTS insurance_usd NUMERIC(14,2);

ALTER TABLE typed_proforma_hbl
ADD COLUMN IF NOT EXISTS taxable_base_usd NUMERIC(14,2);

ALTER TABLE typed_proforma_hbl
ADD COLUMN IF NOT EXISTS cif_border_bob NUMERIC(14,2);

ALTER TABLE typed_proforma_hbl
ADD COLUMN IF NOT EXISTS total_bolivia_bob NUMERIC(14,2);