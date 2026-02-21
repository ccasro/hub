package com.ccasro.hub.modules.booking.application.dto;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;

public record CancelBookingCommand(BookingId bookingId, String reason) {}
