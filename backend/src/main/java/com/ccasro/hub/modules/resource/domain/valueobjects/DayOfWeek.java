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

  public static DayOfWeek fromJava(java.time.DayOfWeek javaDay) {
    return switch (javaDay) {
      case MONDAY -> MON;
      case TUESDAY -> TUE;
      case WEDNESDAY -> WED;
      case THURSDAY -> THU;
      case FRIDAY -> FRI;
      case SATURDAY -> SAT;
      case SUNDAY -> SUN;
    };
  }
}
