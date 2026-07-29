CREATE TABLE opportunity_activities (
    id BIGSERIAL PRIMARY KEY,
    opportunity_id VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(2000),
    activity_date TIMESTAMP NOT NULL,
    source VARCHAR(30) NOT NULL,
    created_by VARCHAR(150),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_opportunity_activities_opportunity_id
    ON opportunity_activities(opportunity_id);

CREATE INDEX idx_opportunity_activities_activity_date
    ON opportunity_activities(activity_date DESC);