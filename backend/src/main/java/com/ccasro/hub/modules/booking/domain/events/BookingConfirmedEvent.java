package com.ccasro.hub.modules.booking.domain.events;

import java.util.UUID;

public record BookingConfirmedEvent(UUID bookingId, String playerEmail) {}
