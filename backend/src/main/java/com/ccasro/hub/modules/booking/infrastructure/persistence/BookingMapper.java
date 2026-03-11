package com.ccasro.hub.modules.booking.infrastructure.persistence;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotRange;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

  public Booking toDomain(BookingEntity e) {
    return Booking.reconstitute(
        BookingId.of(e.getId()),
        ResourceId.of(e.getResourceId()),
        UserId.from(e.getPlayerId()),
        e.getBookingDate(),
        new SlotRange(e.getStartTime(), e.getEndTime()),
        e.getPricePaid(),
        e.getCurrency(),
        e.getStatus(),
        e.getPaymentStatus(),
        e.getCancelledAt(),
        e.getCancelReason(),
        e.getCreatedAt(),
        e.getUpdatedAt(),
        e.getExpiresAt());
  }

  public BookingEntity toEntity(Booking d) {
    BookingEntity e = new BookingEntity();
    fill(e, d);
    return e;
  }

  public void fill(BookingEntity e, Booking d) {
    e.setId(d.getId().value());
    e.setResourceId(d.getResourceId().value());
    e.setPlayerId(d.getPlayerId().value());
    e.setBookingDate(d.getBookingDate());
    e.setStartTime(d.getSlot().startTime());
    e.setEndTime(d.getSlot().endTime());
    e.setPricePaid(d.getPricePaid());
    e.setCurrency(d.getCurrency());
    e.setStatus(d.getStatus());
    e.setPaymentStatus(d.getPaymentStatus());
    e.setCancelledAt(d.getCancelledAt());
    e.setCancelReason(d.getCancelReason());
    e.setCreatedAt(d.getCreatedAt());
    e.setUpdatedAt(d.getUpdatedAt());
    e.setExpiresAt(d.getExpiresAt());
  }
}
