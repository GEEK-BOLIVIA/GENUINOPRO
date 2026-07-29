create table typed_fcl_proforma (
    id uuid primary key,
    code varchar(50),

    customer_id uuid,
    requirement_id uuid,

    customer_name varchar(255),
    customer_phone varchar(50),

    origin_city varchar(150),
    destination_city varchar(150),

    container_type varchar(20),

    merchandise_value_usd numeric(19,2),
    origin_freight_usd numeric(19,2),
    insurance_usd numeric(19,2),

    albo_bob numeric(19,2),
    ada_bob numeric(19,2),
    commission_usd numeric(19,2),

    exchange_rate numeric(19,4),

    subtotal_usd numeric(19,2),
    subtotal_bob numeric(19,2),
    total_bob numeric(19,2),

    currency varchar(10),
    status varchar(50),

    seller_name varchar(150),

    created_at timestamp,
    updated_at timestamp
);