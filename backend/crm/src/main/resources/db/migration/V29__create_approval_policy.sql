CREATE TABLE IF NOT EXISTS approval_policy (
    id UUID PRIMARY KEY,
    proforma_type VARCHAR(255),
    supervisor_limit NUMERIC(19,2),
    commercial_manager_limit NUMERIC(19,2),
    currency VARCHAR(50),
    active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_approval_policy_proforma_type
    ON approval_policy(proforma_type);

CREATE INDEX IF NOT EXISTS idx_approval_policy_active
    ON approval_policy(active);