package com.ccasro.hub.modules.resource.infrastructure.api.dto;

import com.ccasro.hub.modules.resource.domain.valueobjects.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record SetScheduleRequest(
    @NotNull DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {}
