alter table typed_fcl_proforma
    add column if not exists product varchar(255),
    add column if not exists supplier_name varchar(255),
    add column if not exists supplier_phone varchar(100),
    add column if not exists origin_port varchar(150),

    add column if not exists container_count integer,

    add column if not exists fob_usd numeric(19,2),
    add column if not exists exchange_rate_used numeric(19,4),

    add column if not exists maritime_freight_usd numeric(19,2),
    add column if not exists inland_freight_bob numeric(19,2),

    add column if not exists insurance_usd_calculated numeric(19,2),
    add column if not exists cif_bob numeric(19,2),

    add column if not exists ga_percent numeric(10,4),
    add column if not exists iva_percent numeric(10,4),
    add column if not exists ice_percent numeric(10,4),

    add column if not exists ga_bob numeric(19,2),
    add column if not exists iva_bob numeric(19,2),
    add column if not exists ice_bob numeric(19,2),
    add column if not exists customs_taxes_bob numeric(19,2),

    add column if not exists payment_method varchar(50),
    add column if not exists bank_transfer_commission_usd numeric(19,2),

    add column if not exists importer_nit_type varchar(50),
    add column if not exists extra_nit_expenses_bob numeric(19,2),

    add column if not exists genuino_commission_bob numeric(19,2),
    add column if not exists dispatch_agent_commission_bob numeric(19,2),

    add column if not exists total_usd_to_start_order numeric(19,2),
    add column if not exists total_operation_bob numeric(19,2);