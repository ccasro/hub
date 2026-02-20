package com.ccasro.hub.modules.resource.application.dto;

import com.ccasro.hub.modules.resource.domain.valueobjects.DayType;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import java.math.BigDecimal;
import java.time.LocalTime;

public record AddPriceRuleCommand(
    ResourceId resourceId,
    DayType dayType,
    LocalTime startTime,
    LocalTime endTime,
    BigDecimal price,
    String currency) {}
