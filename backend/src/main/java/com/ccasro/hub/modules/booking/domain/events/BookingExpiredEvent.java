package com.ccasro.hub.modules.booking.domain.events;

import java.util.UUID;

public record BookingExpiredEvent(UUID bookingId, String playerEmail) {}
