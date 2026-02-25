package com.ccasro.hub.modules.resource.application.dto;

import com.ccasro.hub.modules.resource.domain.valueobjects.DayOfWeek;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import jakarta.annotation.Nullable;
import java.time.LocalTime;

public record SetScheduleCommand(
    ResourceId resourceId,
    DayOfWeek dayOfWeek,
    @Nullable LocalTime openingTime,
    @Nullable LocalTime closingTime) {}
