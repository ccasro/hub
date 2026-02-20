CREATE TABLE venue (
    id            UUID PRIMARY KEY,
    owner_id      UUID NOT NULL REFERENCES user_profile(id) ON DELETE RESTRICT,
    name          VARCHAR(150) NOT NULL,
    description   TEXT,
    street        VARCHAR(200),
    city          VARCHAR(100),
    country       VARCHAR(100),
    postal_code   VARCHAR(20),
    location      geography(Point, 4326),
    status        VARCHAR(30) NOT NULL DEFAULT 'PENDING_REVIEW',
    reject_reason TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_venue_owner_id ON venue(owner_id);
CREATE INDEX idx_venue_status ON venue(status);
CREATE INDEX idx_venue_location ON venue USING GIST(location);

CREATE TABLE venue_image (
    id            UUID PRIMARY KEY,
    venue_id      UUID NOT NULL REFERENCES venue(id) ON DELETE CASCADE,
    url           VARCHAR(500) NOT NULL,
    public_id     VARCHAR(200) NOT NULL,
    display_order INT DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_venue_image_venue_id ON venue_image(venue_id);