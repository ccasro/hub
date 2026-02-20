package com.ccasro.hub.modules.resource.infrastructure.api.dto;

import com.ccasro.hub.modules.resource.application.dto.SlotAvailabilityDto;
import java.math.BigDecimal;
import java.time.LocalTime;

public record SlotAvailabilityResponse(
    LocalTime startTime, LocalTime endTime, boolean available, BigDecimal price, String currency) {
  public static SlotAvailabilityResponse from(SlotAvailabilityDto dto) {
    return new SlotAvailabilityResponse(
        dto.startTime(), dto.endTime(), dto.available(), dto.price(), dto.currency());
  }
}
