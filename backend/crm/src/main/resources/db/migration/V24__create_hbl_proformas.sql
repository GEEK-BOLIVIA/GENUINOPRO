create table if not exists typed_proforma_hbl (
    proforma_id uuid primary key references typed_proformas(id) on delete cascade,

    issue_date date,
    validity_days integer,
    seller_name varchar(120),
    customer_name varchar(200),
    customer_phone varchar(50),
    customer_address varchar(250),

    product_name varchar(200),
    quantity integer,
    merchandise_value_usd numeric(14,2),
    warehouse_shipping_usd numeric(14,2),
    gross_weight_kg numeric(14,3),
    volume_cbm numeric(14,3),

    ga_percent numeric(8,4),
    iva_percent numeric(8,4),
    ice_percent numeric(8,4),
    sensitive_product boolean not null default false,

    exchange_rate numeric(14,4),
    tax_exchange_rate numeric(14,4),
    supplier_name varchar(200),
    supplier_phone varchar(50),
    payment_method varchar(30),
    importer_nit_type varchar(30),
    customer_pays_in_usd boolean not null default false,

    fob_usd numeric(14,2),
    bank_transfer_commission_usd numeric(14,2),
    maritime_land_freight_usd numeric(14,2),
    sensitive_product_surcharge_usd numeric(14,2),
    subtotal_usd numeric(14,2),

    ga_bob numeric(14,2),
    iva_bob numeric(14,2),
    ice_bob numeric(14,2),
    customs_taxes_bob numeric(14,2),
    albo_customs_clearance_bob numeric(14,2),
    genuino_commission_bob numeric(14,2),
    dispatch_agent_commission_bob numeric(14,2),
    extra_nit_expenses_bob numeric(14,2),
    total_bob numeric(14,2),
    unit_price_bob numeric(14,2),

    calculation_rule_version varchar(50),
    commercial_terms text
);