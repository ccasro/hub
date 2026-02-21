package com.ccasro.hub.modules.booking.infrastructure.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateBookingRequest(
    @NotNull UUID resourceId, @NotNull LocalDate bookingDate, @NotNull LocalTime startTime) {}
