package com.ccasro.hub.modules.resource.application.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

public record SlotAvailabilityDto(
    LocalTime startTime, LocalTime endTime, boolean available, BigDecimal price, String currency) {}
