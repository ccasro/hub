package com.ccasro.hub.modules.booking.infrastructure.persistence;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "booking")
@Getter
@Setter
public class BookingEntity {

  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "resource_id", nullable = false, columnDefinition = "uuid")
  private UUID resourceId;

  @Column(name = "player_id", nullable = false, columnDefinition = "uuid")
  private UUID playerId;

  @Column(name = "booking_date", nullable = false)
  private LocalDate bookingDate;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Column(name = "price_paid", nullable = false, precision = 8, scale = 2)
  private BigDecimal pricePaid;

  @Column(nullable = false, length = 3)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BookingStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false, length = 20)
  private PaymentStatus paymentStatus;

  @Column(name = "cancelled_at")
  private Instant cancelledAt;

  @Column(name = "cancel_reason", columnDefinition = "TEXT")
  private String cancelReason;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Version
  @Column(nullable = false)
  private Long version;

  @Column(name = "expires_at")
  private Instant expiresAt;
}
