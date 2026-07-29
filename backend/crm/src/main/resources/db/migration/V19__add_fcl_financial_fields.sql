ALTER TABLE typed_fcl_proforma
ADD COLUMN total_weight_tn NUMERIC(18,2);

ALTER TABLE typed_fcl_proforma
ADD COLUMN fob_payment_count INTEGER;

ALTER TABLE typed_fcl_proforma
ADD COLUMN customer_pays_in_usd BOOLEAN;

ALTER TABLE typed_fcl_proforma
ADD COLUMN customer_pays_supplier BOOLEAN;