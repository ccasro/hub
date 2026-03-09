package com.ccasro.hub.modules.booking.application.dto;

import com.ccasro.hub.modules.booking.domain.Booking;
import java.util.UUID;

public record MyBookingView(
    Booking booking,
    String resourceName,
    String venueName,
    String venueCity,
    UUID matchRequestId,
    boolean leftMatch) {}
