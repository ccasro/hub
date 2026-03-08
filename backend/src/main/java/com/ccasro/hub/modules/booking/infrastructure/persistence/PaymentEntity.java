package com.ccasro.hub.modules.booking.infrastructure.persistence;

import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment")
@Getter
@Setter
public class PaymentEntity {

  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "booking_id", nullable = false, columnDefinition = "uuid")
  private UUID bookingId;

  @Column(name = "player_id", columnDefinition = "uuid")
  private UUID playerId;

  @Column(name = "stripe_payment_intent_id", unique = true, nullable = false, length = 100)
  private String stripePaymentIntentId;

  @Column(nullable = false, precision = 8, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
