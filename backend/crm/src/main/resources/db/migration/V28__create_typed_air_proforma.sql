CREATE TABLE IF NOT EXISTS typed_proforma_air (

    proforma_id UUID PRIMARY KEY,

    issue_date DATE,
    validity_days INTEGER,

    seller_name VARCHAR(255),

    customer_name VARCHAR(255),
    customer_phone VARCHAR(100),
    customer_address VARCHAR(500),

    product_name VARCHAR(500),
    quantity INTEGER,

    merchandise_value_usd NUMERIC(18,2),
    warehouse_shipping_usd NUMERIC(18,2),

    gross_weight_kg NUMERIC(18,4),
    air_freight_usd NUMERIC(18,2),

    ga_percent NUMERIC(10,4),
    iva_percent NUMERIC(10,4),
    ice_percent NUMERIC(10,4),

    exchange_rate NUMERIC(18,4),
    tax_exchange_rate NUMERIC(18,4),

    supplier_name VARCHAR(255),
    supplier_phone VARCHAR(100),

    payment_method VARCHAR(100),

    input_genuino_commission_bob NUMERIC(18,2),

    commercial_terms TEXT,

    -- RESULTADO USD
    fob_usd NUMERIC(18,2),
    calculated_warehouse_shipping_usd NUMERIC(18,2),
    bank_commission_usd NUMERIC(18,2),
    calculated_air_freight_usd NUMERIC(18,2),
    subtotal_usd NUMERIC(18,2),

    -- LIQUIDACIÓN ADUANERA
    customs_fob_usd NUMERIC(18,2),
    customs_freight_usd NUMERIC(18,2),
    insurance_usd NUMERIC(18,2),
    taxable_base_usd NUMERIC(18,2),
    cif_border_bob NUMERIC(18,2),

    ga_bob NUMERIC(18,2),
    iva_bob NUMERIC(18,2),
    ice_bob NUMERIC(18,2),
    customs_taxes_bob NUMERIC(18,2),

    -- COSTOS BOLIVIA
    anb_form_bob NUMERIC(18,2),
    storage_bob NUMERIC(18,2),
    folder_bob NUMERIC(18,2),
    courier_operational_bob NUMERIC(18,2),

    national_taxes_bob NUMERIC(18,2),
    dispatch_agency_commission_bob NUMERIC(18,2),
    genuino_commission_bob NUMERIC(18,2),

    total_bolivia_bob NUMERIC(18,2),

    -- RESUMEN
    initial_payment_bob NUMERIC(18,2),
    total_bob NUMERIC(18,2),
    unit_price_bob NUMERIC(18,2),

    calculation_rule_version VARCHAR(100)

);