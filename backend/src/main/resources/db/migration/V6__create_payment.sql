CREATE TABLE payment (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id                  UUID NOT NULL REFERENCES booking(id),
    stripe_payment_intent_id    VARCHAR(100) UNIQUE NOT NULL,
    amount                      DECIMAL(8,2) NOT NULL,
    currency                    VARCHAR(3) NOT NULL,
    status                      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
