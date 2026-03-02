package com.ccasro.hub.modules.booking.infrastructure.resource;

import com.ccasro.hub.modules.booking.domain.exception.SlotNotAvailableException;
import com.ccasro.hub.modules.booking.domain.ports.out.ResourceValidationPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.*;
import com.ccasro.hub.modules.resource.infrastructure.persistence.*;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceValidationAdapter implements ResourceValidationPort {

  private final ResourceJpaRepository resourceJpaRepository;
  private final ResourceScheduleJpaRepository scheduleJpaRepository;
  private final ResourcePriceRuleJpaRepository priceRuleJpaRepository;

  @Override
  public ResourceInfo findActiveResource(
      ResourceId resourceId, LocalDate bookingDate, LocalTime startTime) {
    ResourceEntity entity =
        resourceJpaRepository
            .findById(resourceId.value())
            .orElseThrow(SlotNotAvailableException::new);

    if (entity.getStatus() != ResourceStatus.ACTIVE) throw new SlotNotAvailableException();

    DayOfWeek day = DayOfWeek.fromJava(bookingDate.getDayOfWeek());

    ResourceScheduleEntity schedule =
        scheduleJpaRepository
            .findByResourceIdAndDayOfWeek(resourceId.value(), day)
            .orElseThrow(SlotNotAvailableException::new);

    LocalTime endTime = startTime.plusMinutes(entity.getSlotDuration());

    boolean startIsValid = !startTime.isBefore(schedule.getOpeningTime());

    boolean endIsValid = !endTime.isAfter(schedule.getClosingTime());

    if (!startIsValid || !endIsValid) throw new SlotNotAvailableException();

    LocalTime slotStart = schedule.getOpeningTime();
    boolean isValidSlotBoundary = false;

    while (!slotStart.isAfter(schedule.getClosingTime().minusMinutes(entity.getSlotDuration()))) {
      if (slotStart.equals(startTime)) {
        isValidSlotBoundary = true;
        break;
      }
      slotStart = slotStart.plusMinutes(entity.getSlotDuration());
    }

    if (!isValidSlotBoundary) throw new SlotNotAvailableException();

    List<DayType> applicableDayTypes = new ArrayList<>();
    applicableDayTypes.add(DayType.from(day));
    applicableDayTypes.add(day.isWeekend() ? DayType.WEEKEND : DayType.WEEKDAY);

    Optional<ResourcePriceRuleEntity> applicableRule =
        priceRuleJpaRepository.findApplicableRules(resourceId.value(), applicableDayTypes).stream()
            .findFirst();

    BigDecimal price =
        applicableRule.map(ResourcePriceRuleEntity::getPrice).orElse(BigDecimal.ZERO);

    String currency = applicableRule.map(ResourcePriceRuleEntity::getCurrency).orElse("EUR");

    return new ResourceInfo(
        resourceId,
        VenueId.of(entity.getVenueId()),
        new SlotDuration(entity.getSlotDuration()),
        true,
        price,
        currency);
  }
}
