CREATE TABLE match_request (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizer_id          UUID NOT NULL REFERENCES user_profile(id),
    resource_id           UUID NOT NULL REFERENCES resource(id),
    booking_date          DATE NOT NULL,
    start_time            TIME NOT NULL,
    slot_duration_minutes INTEGER NOT NULL,
    format                VARCHAR(20) NOT NULL,
    skill_level           VARCHAR(20) NOT NULL,
    custom_message        TEXT,
    invitation_token      UUID NOT NULL UNIQUE,
    search_lat            DOUBLE PRECISION NOT NULL,
    search_lng            DOUBLE PRECISION NOT NULL,
    search_radius_km      DOUBLE PRECISION NOT NULL,
    price_per_player      DECIMAL(10,2),
    status                VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    expires_at            TIMESTAMPTZ NOT NULL,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE match_player (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_request_id UUID NOT NULL REFERENCES match_request(id) ON DELETE CASCADE,
    player_id        UUID NOT NULL REFERENCES user_profile(id),
    team             VARCHAR(10) NOT NULL,
    role             VARCHAR(20) NOT NULL,
    joined_at        TIMESTAMPTZ NOT NULL,
    absence_reported BOOLEAN NOT NULL DEFAULT FALSE,
    checked_in       BOOLEAN NOT NULL DEFAULT FALSE,
    checked_in_at    TIMESTAMPTZ,
    CONSTRAINT match_player_unique UNIQUE (match_request_id, player_id)
);

CREATE TABLE match_invitation (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_request_id UUID NOT NULL REFERENCES match_request(id),
    player_id        UUID NOT NULL REFERENCES user_profile(id),
    player_email     VARCHAR(255) NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    free_substitute  BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at          TIMESTAMPTZ NOT NULL,
    responded_at     TIMESTAMPTZ,
    UNIQUE (match_request_id, player_id)
);

CREATE INDEX idx_match_request_organizer  ON match_request(organizer_id);
CREATE INDEX idx_match_request_token      ON match_request(invitation_token);
CREATE INDEX idx_match_request_status     ON match_request(status) WHERE status = 'OPEN';
CREATE INDEX idx_match_player_request     ON match_player(match_request_id);
CREATE INDEX idx_invitation_player_id     ON match_invitation(player_id);
CREATE INDEX idx_invitation_match_request ON match_invitation(match_request_id);
