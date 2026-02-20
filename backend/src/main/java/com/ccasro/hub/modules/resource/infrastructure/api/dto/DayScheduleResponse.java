package com.ccasro.hub.modules.resource.infrastructure.api.dto;

import com.ccasro.hub.modules.resource.domain.DaySchedule;
import java.time.LocalTime;

public record DayScheduleResponse(String dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
  public static DayScheduleResponse from(DaySchedule s) {
    return new DayScheduleResponse(s.getDayOfWeek().name(), s.getOpeningTime(), s.getClosingTime());
  }
}
