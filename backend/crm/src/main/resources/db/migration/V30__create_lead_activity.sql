CREATE TABLE IF NOT EXISTS lead_activity (
    id UUID PRIMARY KEY,
    lead_id VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    description TEXT,
    created_by VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_lead_activity_lead_id
    ON lead_activity(lead_id);

CREATE INDEX IF NOT EXISTS idx_lead_activity_created_at
    ON lead_activity(created_at DESC);