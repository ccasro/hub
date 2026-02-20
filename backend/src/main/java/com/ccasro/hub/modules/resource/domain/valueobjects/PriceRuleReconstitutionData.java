package com.ccasro.hub.modules.resource.domain.valueobjects;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record PriceRuleReconstitutionData(
    UUID id,
    DayType dayType,
    LocalTime startTime,
    LocalTime endTime,
    BigDecimal price,
    String currency) {}
