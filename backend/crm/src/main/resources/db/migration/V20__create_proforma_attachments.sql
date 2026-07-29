CREATE TABLE proforma_attachment (
    id UUID PRIMARY KEY,
    typed_proforma_id UUID NOT NULL,
    attachment_type VARCHAR(30) NOT NULL,
    title VARCHAR(255),
    attachment_url TEXT NOT NULL,
    file_name VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_proforma_attachment_typed_proforma
        FOREIGN KEY (typed_proforma_id)
        REFERENCES typed_proformas(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_proforma_attachment_typed_proforma_id
ON proforma_attachment(typed_proforma_id);

CREATE INDEX idx_proforma_attachment_type
ON proforma_attachment(attachment_type);