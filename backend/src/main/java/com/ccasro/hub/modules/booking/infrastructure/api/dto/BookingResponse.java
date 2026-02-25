package com.ccasro.hub.modules.booking.infrastructure.api.dto;

import com.ccasro.hub.modules.booking.application.dto.MyBookingView;
import com.ccasro.hub.modules.booking.application.dto.MyVenueBookingView;
import com.ccasro.hub.modules.booking.domain.Booking;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record BookingResponse(
    UUID id,
    UUID resourceId,
    UUID playerId,
    LocalDate bookingDate,
    LocalTime startTime,
    LocalTime endTime,
    BigDecimal pricePaid,
    String currency,
    String status,
    String paymentStatus,
    Instant cancelledAt,
    String cancelReason,
    Instant createdAt,
    Instant updatedAt,
    Instant expiresAt,
    String resourceName,
    String venueName,
    String venueCity) {

  public static BookingResponse from(Booking b) {
    return base(b, null, null, null);
  }

  public static BookingResponse from(MyBookingView dto) {
    return base(dto.booking(), dto.resourceName(), dto.venueName(), dto.venueCity());
  }

  public static BookingResponse from(MyVenueBookingView dto) {
    return new BookingResponse(
        dto.bookingId(),
        dto.resourceId(),
        dto.playerId(),
        dto.bookingDate(),
        dto.startTime(),
        dto.endTime(),
        dto.pricePaid(),
        dto.currency(),
        dto.status().name(),
        dto.paymentStatus().name(),
        null,
        null,
        null,
        null,
        null,
        dto.resourceName(),
        dto.venueName(),
        dto.city());
  }

  private static BookingResponse base(
      Booking b, String resourceName, String venueName, String venueCity) {
    return new BookingResponse(
        b.getId().value(),
        b.getResourceId().value(),
        b.getPlayerId().value(),
        b.getBookingDate(),
        b.getSlot().startTime(),
        b.getSlot().endTime(),
        b.getPricePaid(),
        b.getCurrency(),
        b.getStatus().name(),
        b.getPaymentStatus().name(),
        b.getCancelledAt(),
        b.getCancelReason(),
        b.getCreatedAt(),
        b.getUpdatedAt(),
        b.getExpiresAt(),
        resourceName,
        venueName,
        venueCity);
  }
}
