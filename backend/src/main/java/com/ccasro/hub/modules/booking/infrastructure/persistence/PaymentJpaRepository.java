package com.ccasro.hub.modules.booking.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
  Optional<PaymentEntity> findByBookingId(UUID bookingId);

  Optional<PaymentEntity> findByStripePaymentIntentId(String paymentIntentId);
}
