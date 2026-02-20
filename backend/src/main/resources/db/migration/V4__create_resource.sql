CREATE TABLE resource (
    id              UUID PRIMARY KEY,
    venue_id        UUID NOT NULL REFERENCES venue(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    resource_type   VARCHAR(30) NOT NULL,
    slot_duration   INT NOT NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDING_REVIEW',
    reject_reason   TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_resource_venue_id ON resource(venue_id);
CREATE INDEX idx_resource_status ON resource(status);

CREATE TABLE resource_schedule (
    id              UUID PRIMARY KEY,
    resource_id     UUID NOT NULL REFERENCES resource(id) ON DELETE CASCADE,
    day_of_week     VARCHAR(10) NOT NULL,
    opening_time    TIME NOT NULL,
    closing_time    TIME NOT NULL
);

CREATE UNIQUE INDEX ux_resource_schedule_unique_slot ON resource_schedule(resource_id, day_of_week, opening_time, closing_time);

CREATE INDEX idx_schedule_resource_id ON resource_schedule(resource_id);

CREATE TABLE resource_price_rule (
    id              UUID PRIMARY KEY,
    resource_id     UUID NOT NULL REFERENCES resource(id) ON DELETE CASCADE,
    day_type        VARCHAR(20) NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    price           DECIMAL(8,2) NOT NULL,
    currency        VARCHAR(3) NOT NULL DEFAULT 'EUR'
);

CREATE INDEX idx_price_rule_resource_id ON resource_price_rule(resource_id);

CREATE TABLE resource_image (
    id              UUID PRIMARY KEY,
    resource_id     UUID NOT NULL REFERENCES resource(id) ON DELETE CASCADE,
    url             VARCHAR(500) NOT NULL,
    public_id       VARCHAR(200) NOT NULL,
    display_order   INT DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_resource_image_resource_id ON resource_image(resource_id);
