CREATE TABLE calculation_parameter (
    id UUID PRIMARY KEY,

    scope VARCHAR(30) NOT NULL,
    code VARCHAR(100) NOT NULL,

    numeric_value NUMERIC(18,6),
    text_value TEXT,

    unit VARCHAR(30) NOT NULL,

    version VARCHAR(50) NOT NULL DEFAULT 'LIQ_2026_09',

    active BOOLEAN NOT NULL DEFAULT TRUE,

    description VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_calculation_parameter
        UNIQUE (scope, code, version)
);

CREATE INDEX idx_calculation_parameter_scope
    ON calculation_parameter(scope);

CREATE INDEX idx_calculation_parameter_code
    ON calculation_parameter(code);

CREATE INDEX idx_calculation_parameter_active
    ON calculation_parameter(active);