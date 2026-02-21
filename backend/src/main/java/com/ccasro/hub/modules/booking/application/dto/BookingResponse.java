package com.ccasro.hub.modules.booking.application.dto;

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
    Instant expiredAt) {
  public static BookingResponse from(Booking b) {
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
        b.getExpiresAt());
  }
}
