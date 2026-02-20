package com.ccasro.hub.modules.resource.infrastructure.api.dto;

import com.ccasro.hub.modules.resource.domain.valueobjects.DayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;

public record AddPriceRuleRequest(
    @NotNull DayType dayType,
    @NotNull LocalTime startTime,
    @NotNull LocalTime endTime,
    @NotNull BigDecimal price,
    @NotBlank String currency) {}
