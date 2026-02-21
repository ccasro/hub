package com.ccasro.hub.modules.resource.infrastructure.booking;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.booking.infrastructure.persistence.BookingJpaRepository;
import com.ccasro.hub.modules.resource.domain.ports.out.BookedSlotsPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotRange;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookedSlotsAdapter implements BookedSlotsPort {

  private final BookingJpaRepository bookingJpaRepository;

  @Override
  public List<SlotRange> findBookedSlots(ResourceId resourceId, LocalDate date) {
    return bookingJpaRepository
        .findByResourceIdAndBookingDateAndStatusNot(
            resourceId.value(), date, BookingStatus.CANCELLED)
        .stream()
        .map(b -> new SlotRange(b.getStartTime(), b.getEndTime()))
        .toList();
  }
}
