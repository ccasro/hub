package com.ccasro.hub.modules.resource.domain.valueobjects;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public class PriceRule {

  private final UUID id;
  private final DayType dayType;
  private final LocalTime startTime;
  private final LocalTime endTime;
  private final BigDecimal price;
  private final String currency;

  private PriceRule(
      UUID id,
      DayType dayType,
      LocalTime startTime,
      LocalTime endTime,
      BigDecimal price,
      String currency) {
    if (price.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Price must be greater than 0");
    if (!endTime.isAfter(startTime))
      throw new IllegalArgumentException("endTime must be later than startTime");
    this.id = id;
    this.dayType = dayType;
    this.startTime = startTime;
    this.endTime = endTime;
    this.price = price;
    this.currency = currency;
  }

  public static PriceRule create(
      DayType dayType, LocalTime startTime, LocalTime endTime, BigDecimal price, String currency) {
    return new PriceRule(UUID.randomUUID(), dayType, startTime, endTime, price, currency);
  }

  public static PriceRule reconstitute(
      UUID id,
      DayType dayType,
      LocalTime startTime,
      LocalTime endTime,
      BigDecimal price,
      String currency) {
    return new PriceRule(id, dayType, startTime, endTime, price, currency);
  }

  public boolean appliesTo(DayOfWeek day, LocalTime time) {
    boolean timeMatches = !time.isBefore(startTime) && time.isBefore(endTime);
    boolean dayMatches =
        switch (dayType) {
          case WEEKDAY -> !day.isWeekend();
          case WEEKEND -> day.isWeekend();
          default -> dayType.name().equals(day.name());
        };
    return timeMatches && dayMatches;
  }

  public UUID getId() {
    return id;
  }

  public DayType getDayType() {
    return dayType;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getCurrency() {
    return currency;
  }
}
