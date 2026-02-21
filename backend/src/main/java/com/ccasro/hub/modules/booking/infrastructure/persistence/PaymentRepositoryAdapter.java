package com.ccasro.hub.modules.booking.infrastructure.persistence;

import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

  private final PaymentJpaRepository jpa;
  private final PaymentMapper mapper;

  @Override
  public Payment save(Payment payment) {
    return mapper.toDomain(jpa.save(mapper.toEntity(payment)));
  }

  @Override
  public Optional<Payment> findByBookingId(BookingId bookingId) {
    return jpa.findByBookingId(bookingId.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<Payment> findByStripePaymentIntentId(String paymentIntentId) {
    return jpa.findByStripePaymentIntentId(paymentIntentId).map(mapper::toDomain);
  }
}
