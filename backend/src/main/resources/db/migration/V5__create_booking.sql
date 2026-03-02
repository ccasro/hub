CREATE TABLE booking (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource_id     UUID NOT NULL REFERENCES resource(id),
    player_id       UUID NOT NULL REFERENCES user_profile(id),
    booking_date    DATE NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    price_paid      DECIMAL(8,2) NOT NULL,
    currency        VARCHAR(3) NOT NULL DEFAULT 'EUR',
    status          VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    payment_status  VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    cancelled_at    TIMESTAMPTZ,
    cancel_reason   TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at      TIMESTAMPTZ,

    CONSTRAINT booking_time_order CHECK (end_time > start_time)
);

ALTER TABLE booking
  ADD CONSTRAINT booking_no_overlap
  EXCLUDE USING gist (
    resource_id WITH =,
    tsrange(
      booking_date::timestamp + start_time,
      booking_date::timestamp + end_time,
      '[)'
    ) WITH &&
  )
  WHERE (status IN ('PENDING_PAYMENT', 'CONFIRMED', 'PENDING_MATCH'));

CREATE INDEX idx_booking_resource_date ON booking (resource_id, booking_date);
CREATE INDEX idx_booking_player_date   ON booking (player_id, booking_date);
CREATE INDEX idx_booking_pending_expires ON booking (expires_at) WHERE status = 'PENDING_PAYMENT';
CREATE INDEX idx_booking_pending_match_expires ON booking (expires_at) WHERE status = 'PENDING_MATCH';