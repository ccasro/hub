package com.ccasro.hub.modules.resource.usecases;

import com.ccasro.hub.modules.resource.application.dto.SlotAvailabilityDto;
import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.exception.ResourceNotFoundException;
import com.ccasro.hub.modules.resource.domain.ports.out.BookedSlotsPort;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetResourceAvailabilityService {

  private final ResourceRepositoryPort resourceRepository;
  private final BookedSlotsPort bookedSlotsPort;

  @Cacheable(value = "slots", key = "#resourceId.value() + '_' + #date")
  @Transactional(readOnly = true)
  public List<SlotAvailabilityDto> execute(ResourceId resourceId, LocalDate date) {
    Resource resource =
        resourceRepository.findById(resourceId).orElseThrow(ResourceNotFoundException::new);

    if (resource.getStatus() != ResourceStatus.ACTIVE)
      throw new IllegalStateException("Resource not available");

    DayOfWeek day = DayOfWeek.fromJava(date.getDayOfWeek());

    List<SlotRange> allSlots = resource.generateSlotsForDay(day);
    if (allSlots.isEmpty()) return Collections.emptyList();

    List<SlotRange> bookedSlots = bookedSlotsPort.findBookedSlots(resourceId, date);

    return allSlots.stream()
        .map(
            slot -> {
              boolean isBooked = bookedSlots.stream().anyMatch(booked -> booked.overlapsWith(slot));

              var ruleOpt = resource.getPriceRuleForSlot(day, slot.startTime());
              BigDecimal price = ruleOpt.map(PriceRule::getPrice).orElse(BigDecimal.ZERO);
              String currency = ruleOpt.map(PriceRule::getCurrency).orElse("EUR");

              return new SlotAvailabilityDto(
                  slot.startTime(), slot.endTime(), !isBooked, price, currency);
            })
        .toList();
  }
}
