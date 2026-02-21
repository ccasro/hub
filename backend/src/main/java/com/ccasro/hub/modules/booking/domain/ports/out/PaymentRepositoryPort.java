package com.ccasro.hub.modules.booking.domain.ports.out;

import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import java.util.Optional;

public interface PaymentRepositoryPort {
  Payment save(Payment payment);

  Optional<Payment> findByBookingId(BookingId bookingId);

  Optional<Payment> findByStripePaymentIntentId(String paymentIntentId);
}
