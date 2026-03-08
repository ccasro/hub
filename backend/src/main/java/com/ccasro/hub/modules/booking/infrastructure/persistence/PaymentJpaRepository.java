package com.ccasro.hub.modules.booking.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
  Optional<PaymentEntity> findByBookingId(UUID bookingId);

  Optional<PaymentEntity> findByBookingIdAndPlayerId(UUID bookingId, UUID playerId);

  List<PaymentEntity> findByBookingIdIn(Collection<UUID> bookingIds);

  Optional<PaymentEntity> findByStripePaymentIntentId(String paymentIntentId);
}
