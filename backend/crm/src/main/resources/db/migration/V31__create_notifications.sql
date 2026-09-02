CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    entity_type VARCHAR(50),
    entity_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notifications_user
    ON notifications(user_id);

CREATE INDEX IF NOT EXISTS idx_notifications_read
    ON notifications(is_read);

CREATE INDEX IF NOT EXISTS idx_notifications_created
    ON notifications(created_at DESC);