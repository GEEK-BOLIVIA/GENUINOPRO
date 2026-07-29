CREATE TABLE client_accounts (
    id UUID PRIMARY KEY,
    lead_id VARCHAR(100) NOT NULL,
    accepted_proforma_id UUID NOT NULL,

    company_name VARCHAR(255),
    contact_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    username VARCHAR(100),

    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_client_accounts_lead_id
ON client_accounts (lead_id);

CREATE UNIQUE INDEX ux_client_accounts_email
ON client_accounts (email)
WHERE email IS NOT NULL;

CREATE INDEX ix_client_accounts_accepted_proforma_id
ON client_accounts (accepted_proforma_id);