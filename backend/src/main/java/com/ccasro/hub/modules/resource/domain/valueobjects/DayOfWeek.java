package com.ccasro.hub.modules.resource.domain.valueobjects;

public enum DayOfWeek {
  MON,
  TUE,
  WED,
  THU,
  FRI,
  SAT,
  SUN;

  public boolean isWeekend() {
    return this == SAT || this == SUN;
  }
}
