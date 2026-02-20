package com.ccasro.hub.modules.resource.application.dto;

import com.ccasro.hub.modules.resource.domain.valueobjects.DayOfWeek;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import java.time.LocalTime;

public record SetScheduleCommand(
    ResourceId resourceId, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {}
