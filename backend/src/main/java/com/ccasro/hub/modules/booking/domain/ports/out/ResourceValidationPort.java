package com.ccasro.hub.modules.booking.domain.ports.out;

import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotDuration;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public interface ResourceValidationPort {
  ResourceInfo findActiveResource(
      ResourceId resourceId, LocalDate bookingDate, LocalTime startTime);

  record ResourceInfo(
      ResourceId id,
      VenueId venueId,
      SlotDuration slotDuration,
      boolean availableOnDay,
      BigDecimal priceForSlot,
      String currency) {}
}
