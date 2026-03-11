CREATE TABLE payment (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id                  UUID NOT NULL REFERENCES booking(id),
    player_id                   UUID REFERENCES user_profile(id),
    stripe_payment_intent_id    VARCHAR(100) UNIQUE NOT NULL,
    amount                      DECIMAL(8,2) NOT NULL,
    currency                    VARCHAR(3) NOT NULL,
    status                      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version                     BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_payment_player_id ON payment(player_id) WHERE player_id IS NOT NULL;
