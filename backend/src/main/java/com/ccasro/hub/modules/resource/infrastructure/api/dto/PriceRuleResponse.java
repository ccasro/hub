package com.ccasro.hub.modules.resource.infrastructure.api.dto;

import com.ccasro.hub.modules.resource.domain.valueobjects.PriceRule;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record PriceRuleResponse(
    UUID id,
    String dayType,
    LocalTime startTime,
    LocalTime endTime,
    BigDecimal price,
    String currency) {
  public static PriceRuleResponse from(PriceRule p) {
    return new PriceRuleResponse(
        p.getId(), p.getDayType().name(),
        p.getStartTime(), p.getEndTime(),
        p.getPrice(), p.getCurrency());
  }
}
