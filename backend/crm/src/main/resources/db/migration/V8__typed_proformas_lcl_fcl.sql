create extension if not exists pgcrypto;

-- =========================
-- PROFORMAS TIPIFICADAS
-- =========================

create table if not exists typed_proformas (
    id uuid primary key default gen_random_uuid(),

    opportunity_id varchar(50) not null,
    customer_id varchar(50),

    type varchar(20) not null,              -- LCL, FCL, HBL, AEREO
    status varchar(20) not null,            -- DRAFT, IN_REVIEW, APPROVED, REJECTED

    currency varchar(10) not null,          -- USD, BOB
    total numeric(14,2) not null default 0,
    estimated_profit numeric(14,2) not null default 0,

    version integer not null default 1,
    notes text,

    created_by varchar(100) not null,
    created_at timestamp not null default now(),
    updated_by varchar(100),
    updated_at timestamp,

    approved_by varchar(100),
    approved_at timestamp,
    rejection_reason text
);

create index if not exists idx_typed_proformas_opportunity_id on typed_proformas(opportunity_id);
create index if not exists idx_typed_proformas_customer_id on typed_proformas(customer_id);
create index if not exists idx_typed_proformas_type on typed_proformas(type);
create index if not exists idx_typed_proformas_status on typed_proformas(status);


-- =========================
-- LCL
-- =========================

create table if not exists typed_proforma_lcl (
    proforma_id uuid primary key references typed_proformas(id) on delete cascade,

    issue_date date,
    validity_days integer,

    seller_name varchar(120),
    customer_name varchar(200),
    customer_phone varchar(50),
    customer_address varchar(250),

    origin_country varchar(120),
    origin_city varchar(120),
    destination_country varchar(120),
    destination_city varchar(120),
    port_origin varchar(150),
    port_destination varchar(150),

    incoterm varchar(20),
    cargo_type varchar(100),
    transit_time varchar(100),
    carrier_name varchar(150),
    agent_name varchar(150),

    package_count integer,
    gross_weight_kg numeric(14,3),
    volume_cbm numeric(14,3),
    cargo_description text,

    freight_rate numeric(14,2),
    origin_charges numeric(14,2),
    destination_charges numeric(14,2),
    handling_charges numeric(14,2),
    documentation_charges numeric(14,2),
    customs_charges numeric(14,2),
    insurance_charges numeric(14,2),
    other_charges numeric(14,2),
    commission_amount numeric(14,2),
    margin_amount numeric(14,2),

    subtotal_costs numeric(14,2),
    subtotal_sell numeric(14,2),
    estimated_profit numeric(14,2),

    commercial_terms text
);


-- =========================
-- FCL
-- =========================

create table if not exists typed_proforma_fcl (
    proforma_id uuid primary key references typed_proformas(id) on delete cascade,

    issue_date date,
    seller_name varchar(120),
    customer_name varchar(200),
    customer_phone varchar(50),
    customer_address varchar(250),

    product_name varchar(200),
    gross_weight_kg numeric(14,3),
    fob_value numeric(14,2),
    exchange_rate numeric(14,4),

    payment_count integer,
    payment_route varchar(50),              -- ALIBABA, CHILE, etc.
    customer_pays_in_usd boolean,
    customer_pays_supplier boolean,

    sea_freight numeric(14,2),
    land_freight numeric(14,2),
    tariff_rate numeric(14,2),
    iva_amount numeric(14,2),
    ice_amount numeric(14,2),

    various_expenses numeric(14,2),
    extra_nit_expenses numeric(14,2),
    customs_broker_commission numeric(14,2),

    container_count integer,
    nit_arrival_target varchar(50),         -- CLIENTE / LEANDRO

    giro_commission numeric(14,2),
    genuino_commission numeric(14,2),
    albo_fee numeric(14,2),
    ada_policy_fee numeric(14,2),
    maritime_fixed_surcharge numeric(14,2),

    subtotal_costs numeric(14,2),
    subtotal_sell numeric(14,2),
    estimated_profit numeric(14,2),

    commercial_terms text
);


-- =========================
-- LÍNEAS DE CARGO
-- =========================

create table if not exists typed_proforma_charge_lines (
    id uuid primary key default gen_random_uuid(),
    proforma_id uuid not null references typed_proformas(id) on delete cascade,

    line_group varchar(30) not null,        -- COST, SELL, TAX, COMMISSION, EXTRA
    code varchar(50),
    description varchar(200) not null,

    quantity numeric(14,3) not null default 1,
    unit_price numeric(14,2) not null default 0,
    total numeric(14,2) not null default 0,

    editable boolean not null default false,
    sort_order integer not null default 0
);

create index if not exists idx_typed_proforma_charge_lines_proforma_id
    on typed_proforma_charge_lines(proforma_id);


-- =========================
-- SNAPSHOT DE CÁLCULO
-- =========================

create table if not exists typed_proforma_calculation_snapshot (
    id uuid primary key default gen_random_uuid(),
    proforma_id uuid not null references typed_proformas(id) on delete cascade,

    calculation_version integer not null default 1,
    input_json text not null,
    output_json text not null,

    created_at timestamp not null default now(),
    created_by varchar(100) not null
);

create index if not exists idx_typed_proforma_calc_snapshot_proforma_id
    on typed_proforma_calculation_snapshot(proforma_id);


-- =========================
-- REGLAS DE NEGOCIO / CONFIGURACIÓN
-- =========================

create table if not exists typed_pricing_rule (
    id uuid primary key default gen_random_uuid(),

    proforma_type varchar(20) not null,     -- LCL, FCL, etc.
    rule_code varchar(50) not null,
    rule_name varchar(150) not null,
    rule_kind varchar(30) not null,         -- FIXED, PERCENTAGE, RANGE, FORMULA, FLAG

    is_active boolean not null default true,
    config_json text not null,

    created_at timestamp not null default now(),
    updated_at timestamp
);

create unique index if not exists uq_typed_pricing_rule_type_code
    on typed_pricing_rule(proforma_type, rule_code);


-- =========================
-- DATOS SEMILLA INICIALES FCL
-- =========================

insert into typed_pricing_rule (proforma_type, rule_code, rule_name, rule_kind, is_active, config_json)
values
    ('FCL', 'COMM_GIRO_ALIBABA', 'Comisión de giro vía Alibaba', 'PERCENTAGE', true, '{"percentage": 0.00}'),
    ('FCL', 'COMM_GIRO_CHILE', 'Comisión de giro vía Chile', 'PERCENTAGE', true, '{"percentage": 0.00}'),
    ('FCL', 'COMM_GENUINO_FOB_RANGE', 'Comisión Genuino por rango FOB', 'RANGE', true, '{"ranges":[]}'),
    ('FCL', 'ALBO_PER_CONTAINER', 'Costo ALBO por contenedor', 'FIXED', true, '{"amount": 0.00}'),
    ('FCL', 'ADA_PER_POLICY', 'Costo ADA por póliza', 'FIXED', true, '{"amount": 0.00}'),
    ('FCL', 'MARITIME_FIXED_SURCHARGE', 'Recargo fijo marítimo', 'FIXED', true, '{"amount": 0.00}'),
    ('FCL', 'NIT_LEANDRO_EXTRA', 'Costo extra por arribo al NIT de Leandro', 'FIXED', true, '{"amount": 0.00}')
on conflict do nothing;