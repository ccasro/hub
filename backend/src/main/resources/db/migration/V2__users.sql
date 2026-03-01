CREATE TABLE user_profile (

    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    auth0_id       VARCHAR(128) UNIQUE NOT NULL,
    email          VARCHAR(255) UNIQUE,
    email_verified BOOLEAN DEFAULT FALSE,

    display_name   VARCHAR(100),
    description    TEXT,
    phone_number   VARCHAR(20),

    avatar_url     VARCHAR(500),
    avatar_public_id VARCHAR(200),

    role VARCHAR(20) NOT NULL DEFAULT 'PLAYER'
        CHECK (role IN ('PLAYER', 'OWNER', 'ADMIN')),

    owner_request_status  VARCHAR(20)  DEFAULT NULL,

    preferred_sport VARCHAR(50)
        CHECK (preferred_sport IN ('PADEL', 'TENNIS', 'SQUASH', 'BADMINTON')),

    skill_level VARCHAR(20)
        CHECK (skill_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),

    city         VARCHAR(100),
    country_code VARCHAR(3),

    active BOOLEAN DEFAULT TRUE,
    onboarding_completed BOOLEAN DEFAULT FALSE,

    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_login_at TIMESTAMPTZ
);