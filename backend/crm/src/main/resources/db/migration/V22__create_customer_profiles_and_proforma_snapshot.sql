-- =========================================================
-- CATÁLOGO DE CIUDADES DE BOLIVIA
-- =========================================================

CREATE TABLE IF NOT EXISTS bolivia_city (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    department VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INTEGER NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_bolivia_city_name_department
    ON bolivia_city (name, department);

INSERT INTO bolivia_city (
    code,
    name,
    department,
    active,
    sort_order
)
VALUES
    ('LPZ_LA_PAZ', 'La Paz', 'La Paz', TRUE, 10),
    ('LPZ_EL_ALTO', 'El Alto', 'La Paz', TRUE, 20),
    ('SCZ_SANTA_CRUZ', 'Santa Cruz de la Sierra', 'Santa Cruz', TRUE, 30),
    ('CBB_COCHABAMBA', 'Cochabamba', 'Cochabamba', TRUE, 40),
    ('CHQ_SUCRE', 'Sucre', 'Chuquisaca', TRUE, 50),
    ('ORU_ORURO', 'Oruro', 'Oruro', TRUE, 60),
    ('PTS_POTOSI', 'Potosí', 'Potosí', TRUE, 70),
    ('TJA_TARIJA', 'Tarija', 'Tarija', TRUE, 80),
    ('BEN_TRINIDAD', 'Trinidad', 'Beni', TRUE, 90),
    ('PND_COBIJA', 'Cobija', 'Pando', TRUE, 100),
    ('SCZ_MONTERO', 'Montero', 'Santa Cruz', TRUE, 110),
    ('SCZ_WARNES', 'Warnes', 'Santa Cruz', TRUE, 120),
    ('CBB_QUILLACOLLO', 'Quillacollo', 'Cochabamba', TRUE, 130),
    ('CBB_SACABA', 'Sacaba', 'Cochabamba', TRUE, 140),
    ('TJA_YACUIBA', 'Yacuiba', 'Tarija', TRUE, 150)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    department = EXCLUDED.department,
    active = EXCLUDED.active,
    sort_order = EXCLUDED.sort_order;


-- =========================================================
-- PERFIL DEL CLIENTE ASOCIADO AL LEAD
-- =========================================================

CREATE TABLE IF NOT EXISTS lead_customer_profile (
    lead_id TEXT PRIMARY KEY
        REFERENCES lead_inbox(id)
        ON DELETE CASCADE,

    customer_type VARCHAR(30) NOT NULL DEFAULT 'UNDEFINED',

    -- Persona natural
    full_name VARCHAR(200),
    city_code VARCHAR(50)
        REFERENCES bolivia_city(code),
    mobile_phone VARCHAR(50),

    -- Empresa
    legal_name VARCHAR(250),
    tax_id VARCHAR(100),
    company_phone VARCHAR(50),
    address_text VARCHAR(500),
    maps_url TEXT,
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    legal_representative_name VARCHAR(250),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_lead_customer_profile_type
        CHECK (
            customer_type IN (
                'UNDEFINED',
                'NATURAL_PERSON',
                'COMPANY'
            )
        )
);

CREATE INDEX IF NOT EXISTS idx_lead_customer_profile_type
    ON lead_customer_profile(customer_type);

CREATE INDEX IF NOT EXISTS idx_lead_customer_profile_city
    ON lead_customer_profile(city_code);

CREATE INDEX IF NOT EXISTS idx_lead_customer_profile_tax_id
    ON lead_customer_profile(tax_id);


-- =========================================================
-- COPIA HISTÓRICA DE DATOS DEL CLIENTE EN LA PROFORMA
-- =========================================================

CREATE TABLE IF NOT EXISTS proforma_customer_snapshot (
    proforma_id UUID PRIMARY KEY
        REFERENCES typed_proformas(id)
        ON DELETE CASCADE,

    customer_type VARCHAR(30) NOT NULL,

    -- Persona natural
    full_name VARCHAR(200),
    city_code VARCHAR(50),
    city_name VARCHAR(120),
    department_name VARCHAR(120),
    mobile_phone VARCHAR(50),

    -- Empresa
    legal_name VARCHAR(250),
    tax_id VARCHAR(100),
    company_phone VARCHAR(50),
    address_text VARCHAR(500),
    maps_url TEXT,
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    legal_representative_name VARCHAR(250),

    source_lead_id TEXT,
    captured_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT ck_proforma_customer_snapshot_type
        CHECK (
            customer_type IN (
                'NATURAL_PERSON',
                'COMPANY'
            )
        )
);

CREATE INDEX IF NOT EXISTS idx_proforma_customer_snapshot_type
    ON proforma_customer_snapshot(customer_type);

CREATE INDEX IF NOT EXISTS idx_proforma_customer_snapshot_tax_id
    ON proforma_customer_snapshot(tax_id);