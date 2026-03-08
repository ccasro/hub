package com.ccasro.hub.modules.booking.domain.ports.out;

import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PaymentRepositoryPort {
  Payment save(Payment payment);

  Optional<Payment> findByBookingId(BookingId bookingId);

  Optional<Payment> findByBookingIdAndPlayerId(BookingId bookingId, UserId playerId);

  Optional<Payment> findByStripePaymentIntentId(String paymentIntentId);

  Map<BookingId, Payment> findByBookingIds(Set<BookingId> bookingIds);
}
