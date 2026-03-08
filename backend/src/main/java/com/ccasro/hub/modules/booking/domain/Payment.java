package com.ccasro.hub.modules.booking.domain;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentId;
import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentStatus;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

public class Payment {

  private final PaymentId id;
  private final BookingId bookingId;
  private final UserId playerId;
  private String stripePaymentIntentId;
  private final BigDecimal amount;
  private final String currency;
  private PaymentStatus status;
  private Instant createdAt;
  private Instant updatedAt;

  private Payment(
      PaymentId id,
      BookingId bookingId,
      UserId playerId,
      String stripePaymentIntentId,
      BigDecimal amount,
      String currency,
      Instant now) {
    this.id = id;
    this.bookingId = bookingId;
    this.playerId = playerId;
    this.stripePaymentIntentId = stripePaymentIntentId;
    this.amount = amount;
    this.currency = currency;
    this.status = PaymentStatus.PENDING;
    this.createdAt = now;
    this.updatedAt = now;
  }

  /** For direct bookings (single payer). */
  public static Payment create(
      BookingId bookingId,
      String stripePaymentIntentId,
      BigDecimal amount,
      String currency,
      Clock clock) {
    return new Payment(
        PaymentId.generate(),
        bookingId,
        null,
        stripePaymentIntentId,
        amount,
        currency,
        clock.instant());
  }

  /** For match split payments (one record per player). */
  public static Payment createForPlayer(
      BookingId bookingId,
      UserId playerId,
      String stripePaymentIntentId,
      BigDecimal amount,
      String currency,
      Clock clock) {
    return new Payment(
        PaymentId.generate(),
        bookingId,
        playerId,
        stripePaymentIntentId,
        amount,
        currency,
        clock.instant());
  }

  public static Payment reconstitute(
      PaymentId id,
      BookingId bookingId,
      UserId playerId,
      String stripePaymentIntentId,
      BigDecimal amount,
      String currency,
      PaymentStatus status,
      Instant createdAt,
      Instant updatedAt) {
    Payment p =
        new Payment(id, bookingId, playerId, stripePaymentIntentId, amount, currency, createdAt);
    p.status = status;
    p.updatedAt = updatedAt;
    return p;
  }

  public void markAsPaid(Clock clock) {
    if (this.status == PaymentStatus.PAID) return;
    this.status = PaymentStatus.PAID;
    this.updatedAt = clock.instant();
  }

  public void markAsRefunded(Clock clock) {
    if (this.status == PaymentStatus.REFUNDED) return;
    this.status = PaymentStatus.REFUNDED;
    this.updatedAt = clock.instant();
  }

  public void markAsFailed() {
    if (this.status == PaymentStatus.FAILED) return;
    this.status = PaymentStatus.FAILED;
  }

  public PaymentId getId() {
    return id;
  }

  public BookingId getBookingId() {
    return bookingId;
  }

  public UserId getPlayerId() {
    return playerId;
  }

  public String getStripePaymentIntentId() {
    return stripePaymentIntentId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
