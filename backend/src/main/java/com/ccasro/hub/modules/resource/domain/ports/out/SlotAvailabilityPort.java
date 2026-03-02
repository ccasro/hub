package com.ccasro.hub.modules.resource.domain.ports.out;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface SlotAvailabilityPort {

  record SlotLite(LocalTime startTime, LocalTime endTime, double price, String currency) {}

  List<SlotLite> findAvailableSlots(UUID resourceId, LocalDate date, int durationMinutes);
}
