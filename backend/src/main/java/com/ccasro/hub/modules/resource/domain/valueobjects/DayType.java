package com.ccasro.hub.modules.resource.domain.valueobjects;

public enum DayType {
  WEEKDAY,
  WEEKEND,
  MON,
  TUE,
  WED,
  THU,
  FRI,
  SAT,
  SUN;

  public static DayType from(DayOfWeek day) {
    return switch (day) {
      case MON -> MON;
      case TUE -> TUE;
      case WED -> WED;
      case THU -> THU;
      case FRI -> FRI;
      case SAT -> SAT;
      case SUN -> SUN;
    };
  }
}
