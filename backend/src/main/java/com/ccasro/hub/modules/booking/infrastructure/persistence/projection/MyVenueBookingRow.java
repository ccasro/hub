package com.ccasro.hub.modules.booking.infrastructure.persistence.projection;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record MyVenueBookingRow(
    UUID bookingId,
    UUID resourceId,
    UUID playerId,
    LocalDate bookingDate,
    LocalTime startTime,
    LocalTime endTime,
    BigDecimal pricePaid,
    String currency,
    BookingStatus status,
    PaymentStatus paymentStatus,
    Instant createdAt,
    String resourceName,
    UUID venueId,
    String venueName,
    String city) {}
