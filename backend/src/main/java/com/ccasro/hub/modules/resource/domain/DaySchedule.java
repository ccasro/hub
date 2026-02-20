package com.ccasro.hub.modules.resource.domain;

import com.ccasro.hub.modules.resource.domain.valueobjects.DayOfWeek;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotDuration;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotRange;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DaySchedule {

  private final UUID id;
  private final DayOfWeek dayOfWeek;
  private final LocalTime openingTime;
  private final LocalTime closingTime;

  public DaySchedule(UUID id, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
    if (closingTime.isBefore(openingTime) || closingTime.equals(openingTime))
      throw new IllegalArgumentException(
          "closingTime must be later than openingTime for " + dayOfWeek);
    this.id = id;
    this.dayOfWeek = dayOfWeek;
    this.openingTime = openingTime;
    this.closingTime = closingTime;
  }

  public static DaySchedule create(
      DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime) {
    return new DaySchedule(UUID.randomUUID(), dayOfWeek, openingTime, closingTime);
  }

  public List<SlotRange> generateSlots(SlotDuration duration) {
    List<SlotRange> slots = new ArrayList<>();
    LocalTime current = openingTime;
    while (!current.plusMinutes(duration.minutes()).isAfter(closingTime)) {
      slots.add(new SlotRange(current, current.plusMinutes(duration.minutes())));
      current = current.plusMinutes(duration.minutes());
    }
    return slots;
  }

  public UUID getId() {
    return id;
  }

  public DayOfWeek getDayOfWeek() {
    return dayOfWeek;
  }

  public LocalTime getOpeningTime() {
    return openingTime;
  }

  public LocalTime getClosingTime() {
    return closingTime;
  }
}
