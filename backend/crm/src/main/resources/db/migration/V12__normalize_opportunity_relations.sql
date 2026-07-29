ALTER TABLE commercial_task
ADD COLUMN IF NOT EXISTS opportunity_id VARCHAR(100);

ALTER TABLE commercial_task
ADD COLUMN IF NOT EXISTS proforma_id UUID;

ALTER TABLE opportunity_activities
ADD COLUMN IF NOT EXISTS proforma_id UUID;

CREATE INDEX IF NOT EXISTS idx_commercial_task_opportunity
ON commercial_task(opportunity_id);

CREATE INDEX IF NOT EXISTS idx_commercial_task_proforma
ON commercial_task(proforma_id);

CREATE INDEX IF NOT EXISTS idx_activity_proforma
ON opportunity_activities(proforma_id);