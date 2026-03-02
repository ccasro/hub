package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.booking.infrastructure.persistence.BookingJpaRepository;
import com.ccasro.hub.modules.resource.domain.ports.out.SlotAvailabilityPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.DayOfWeek;
import com.ccasro.hub.modules.resource.domain.valueobjects.DayType;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlotAvailabilityAdapter implements SlotAvailabilityPort {

  private final ResourceJpaRepository resourceRepository;
  private final ResourceScheduleJpaRepository scheduleRepository;
  private final ResourcePriceRuleJpaRepository priceRuleRepository;
  private final BookingJpaRepository bookingRepository;

  @Override
  public List<SlotLite> findAvailableSlots(UUID resourceId, LocalDate date, int durationMinutes) {

    var resource = resourceRepository.findById(resourceId).orElse(null);
    if (resource == null || resource.getStatus() != ResourceStatus.ACTIVE) return List.of();

    DayOfWeek day = DayOfWeek.fromJava(date.getDayOfWeek());
    Optional<ResourceScheduleEntity> scheduleOpt =
        scheduleRepository.findByResourceIdAndDayOfWeek(resourceId, day);
    if (scheduleOpt.isEmpty()) return List.of();

    var schedule = scheduleOpt.get();
    int slotMin = durationMinutes > 0 ? durationMinutes : resource.getSlotDuration();

    var bookedRanges =
        bookingRepository
            .findByResourceIdAndBookingDateAndStatusNot(resourceId, date, BookingStatus.CANCELLED)
            .stream()
            .map(b -> new LocalTime[] {b.getStartTime(), b.getEndTime()})
            .toList();

    List<DayType> dayTypes =
        List.of(DayType.from(day), day.isWeekend() ? DayType.WEEKEND : DayType.WEEKDAY);
    List<ResourcePriceRuleEntity> allRules =
        priceRuleRepository.findApplicableRules(resourceId, dayTypes);

    LocalTime cursor = schedule.getOpeningTime();
    LocalTime closing = schedule.getClosingTime();

    if (slotMin <= 0) {
      return List.of();
    }
    if (!closing.isAfter(cursor)) {
      return List.of();
    }

    List<SlotLite> available = new ArrayList<>();

    while (cursor.isBefore(closing) && !cursor.plusMinutes(slotMin).isAfter(closing)) {
      LocalTime slotEnd = cursor.plusMinutes(slotMin);
      final LocalTime slotStart = cursor;

      boolean isBooked =
          bookedRanges.stream().anyMatch(b -> slotStart.isBefore(b[1]) && slotEnd.isAfter(b[0]));

      if (!isBooked) {
        Optional<ResourcePriceRuleEntity> rule =
            allRules.stream()
                .filter(
                    r ->
                        !slotStart.isBefore(r.getStartTime()) && slotStart.isBefore(r.getEndTime()))
                .findFirst();

        double price = rule.map(r -> r.getPrice().doubleValue()).orElse(0.0);
        String currency = rule.map(ResourcePriceRuleEntity::getCurrency).orElse("EUR");

        available.add(new SlotLite(slotStart, slotEnd, price, currency));
      }

      int advance = resource.getSlotDuration() > 0 ? resource.getSlotDuration() : slotMin;
      cursor = cursor.plusMinutes(advance);

      if (!cursor.isAfter(slotStart)) {
        break;
      }
    }

    return available;
  }
}
