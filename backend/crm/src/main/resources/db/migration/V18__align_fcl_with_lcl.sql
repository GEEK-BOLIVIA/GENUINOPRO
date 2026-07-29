ALTER TABLE typed_fcl_proforma
ALTER COLUMN customer_id TYPE VARCHAR(80)
USING customer_id::text;

ALTER TABLE typed_fcl_proforma
RENAME COLUMN requirement_id TO opportunity_id;

ALTER TABLE typed_fcl_proforma
ALTER COLUMN opportunity_id TYPE VARCHAR(80)
USING opportunity_id::text;
