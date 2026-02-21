package com.ccasro.hub.modules.booking.application.dto;

import com.ccasro.hub.modules.booking.domain.Booking;

public record CreateBookingResult(Booking booking, String clientSecret) {}
