package com.ccasro.hub.modules.booking.domain.events;

import java.util.UUID;

public record BookingCancelledEvent(UUID bookingId, String playerEmail) {}
