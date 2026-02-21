package com.ccasro.hub.modules.booking.application.dto;

import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateBookingCommand(
    ResourceId resourceId, LocalDate bookingDate, LocalTime startTime) {}
