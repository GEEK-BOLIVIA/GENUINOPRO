CREATE TABLE IF NOT EXISTS commercial_task (
    id UUID PRIMARY KEY,
    lead_id VARCHAR(255),
    title VARCHAR(255),
    description TEXT,
    status VARCHAR(255),
    priority VARCHAR(255),
    assigned_to VARCHAR(255),
    due_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_commercial_task_lead
    ON commercial_task(lead_id);

CREATE INDEX IF NOT EXISTS idx_commercial_task_assigned_to
    ON commercial_task(assigned_to);

CREATE INDEX IF NOT EXISTS idx_commercial_task_status
    ON commercial_task(status);