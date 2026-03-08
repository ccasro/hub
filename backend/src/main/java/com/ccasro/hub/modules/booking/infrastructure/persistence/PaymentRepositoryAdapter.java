package com.ccasro.hub.modules.booking.infrastructure.persistence;

import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
  public Optional<Payment> findByBookingIdAndPlayerId(BookingId bookingId, UserId playerId) {
    return jpa.findByBookingIdAndPlayerId(bookingId.value(), playerId.value())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Payment> findByStripePaymentIntentId(String paymentIntentId) {
    return jpa.findByStripePaymentIntentId(paymentIntentId).map(mapper::toDomain);
  }

  @Override
  public Map<BookingId, Payment> findByBookingIds(Set<BookingId> bookingIds) {
    Set<UUID> ids = bookingIds.stream().map(BookingId::value).collect(Collectors.toSet());
    return jpa.findByBookingIdIn(ids).stream()
        .collect(Collectors.toMap(e -> new BookingId(e.getBookingId()), mapper::toDomain));
  }
}
