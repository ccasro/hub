package com.ccasro.hub.modules.booking.application.dto;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record MyVenueBookingView(
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
    String resourceName,
    String venueName,
    String city) {}
