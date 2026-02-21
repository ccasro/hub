package com.ccasro.hub.modules.booking.infrastructure.api.dto;

import com.ccasro.hub.modules.booking.application.dto.BookingResponse;

public record CreateBookingResponse(BookingResponse booking, String clientSecret) {}
