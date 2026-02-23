package com.ccasro.hub.modules.booking.application.dto;

import com.ccasro.hub.modules.booking.domain.Booking;

public record MyBookingView(
    Booking booking, String resourceName, String venueName, String venueCity) {}
