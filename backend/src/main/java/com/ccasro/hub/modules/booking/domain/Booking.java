package com.ccasro.hub.modules.booking.domain;

import com.ccasro.hub.modules.booking.domain.exception.BookingCancellationNotAllowedException;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentStatus;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotRange;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.math.BigDecimal;
import java.time.*;

public class Booking {

  private final BookingId id;
  private final ResourceId resourceId;
  private final UserId playerId;
  private final LocalDate bookingDate;
  private final SlotRange slot;
  private final BigDecimal pricePaid;
  private final String currency;
  private BookingStatus status;
  private PaymentStatus paymentStatus;
  private Instant cancelledAt;
  private String cancelReason;
  private final Instant createdAt;
  private Instant updatedAt;
  private Instant expiresAt;

  private Booking(
      BookingId id,
      ResourceId resourceId,
      UserId playerId,
      LocalDate bookingDate,
      SlotRange slot,
      BigDecimal pricePaid,
      String currency,
      Instant now,
      Instant expiresAt) {
    this.id = id;
    this.resourceId = resourceId;
    this.playerId = playerId;
    this.bookingDate = bookingDate;
    this.slot = slot;
    this.pricePaid = pricePaid;
    this.currency = currency;
    this.status = BookingStatus.PENDING_PAYMENT;
    this.paymentStatus = PaymentStatus.PENDING;
    this.cancelledAt = null;
    this.cancelReason = null;
    this.createdAt = now;
    this.updatedAt = now;
    this.expiresAt = expiresAt;
  }

  public static Booking create(
      ResourceId resourceId,
      UserId playerId,
      LocalDate bookingDate,
      SlotRange slot,
      BigDecimal pricePaid,
      String currency,
      Duration holdDuration,
      Clock clock) {

    Instant now = clock.instant();
    Instant expiresAt = now.plus(holdDuration);

    return new Booking(
        BookingId.generate(),
        resourceId,
        playerId,
        bookingDate,
        slot,
        pricePaid,
        currency,
        now,
        expiresAt);
  }

  public static Booking reconstitute(
      BookingId id,
      ResourceId resourceId,
      UserId playerId,
      LocalDate bookingDate,
      SlotRange slot,
      BigDecimal pricePaid,
      String currency,
      BookingStatus status,
      PaymentStatus paymentStatus,
      Instant cancelledAt,
      String cancelReason,
      Instant createdAt,
      Instant updatedAt,
      Instant expiresAt) {
    Booking b =
        new Booking(
            id, resourceId, playerId, bookingDate, slot, pricePaid, currency, createdAt, expiresAt);
    b.status = status;
    b.paymentStatus = paymentStatus;
    b.cancelledAt = cancelledAt;
    b.cancelReason = cancelReason;
    b.updatedAt = updatedAt;
    b.expiresAt = expiresAt;
    return b;
  }

  public static Booking createForMatch(
      ResourceId resourceId,
      UserId organizerId,
      LocalDate bookingDate,
      SlotRange slot,
      BigDecimal price,
      String currency,
      Instant expiresAt,
      Clock clock) {

    Instant now = clock.instant();
    Booking b =
        new Booking(
            BookingId.generate(),
            resourceId,
            organizerId,
            bookingDate,
            slot,
            price,
            currency,
            now,
            expiresAt);
    b.status = BookingStatus.PENDING_MATCH;
    b.paymentStatus = PaymentStatus.PENDING;
    return b;
  }

  public void confirmMatch(Clock clock) {
    if (this.status != BookingStatus.PENDING_MATCH)
      throw new IllegalStateException("Booking is not in PENDING_MATCH status");
    this.status = BookingStatus.CONFIRMED;
    this.paymentStatus = PaymentStatus.PAID;
    this.expiresAt = null;
    this.updatedAt = clock.instant();
  }

  public void revertToPendingMatch(Clock clock) {
    if (this.status != BookingStatus.CONFIRMED)
      throw new IllegalStateException("Booking is not CONFIRMED");
    this.status = BookingStatus.PENDING_MATCH;
    this.paymentStatus = PaymentStatus.PENDING;
    this.updatedAt = clock.instant();
  }

  public void cancelMatch(Clock clock) {
    if (this.status != BookingStatus.PENDING_MATCH && this.status != BookingStatus.CONFIRMED)
      throw new IllegalStateException("Booking is not in PENDING_MATCH or CONFIRMED status");
    this.status = BookingStatus.CANCELLED;
    if (this.paymentStatus == PaymentStatus.PAID) {
      this.paymentStatus = PaymentStatus.REFUNDED;
    }
    this.cancelReason = "Match cancelled or expired";
    this.cancelledAt = clock.instant();
    this.updatedAt = clock.instant();
  }

  public void confirmPayment(Clock clock) {
    if (this.paymentStatus == PaymentStatus.PAID)
      throw new IllegalStateException("Payment is already confirmed");
    if (this.status == BookingStatus.CANCELLED)
      throw new IllegalStateException("Cannot confirm a cancelled booking");

    this.paymentStatus = PaymentStatus.PAID;
    this.status = BookingStatus.CONFIRMED;
    this.expiresAt = null;
    this.updatedAt = clock.instant();
  }

  public void expireHold(Clock clock) {
    if (this.status != BookingStatus.PENDING_PAYMENT) return;
    markPaymentFailed(clock);
  }

  public void markPaymentFailed(Clock clock) {
    this.paymentStatus = PaymentStatus.FAILED;
    this.status = BookingStatus.CANCELLED;
    this.cancelReason = "Payment not completed";
    this.cancelledAt = clock.instant();
    this.updatedAt = clock.instant();
    this.expiresAt = null;
  }

  public void cancel(String reason, Duration cancellationDeadline, Clock clock) {
    if (this.status == BookingStatus.CANCELLED)
      throw new IllegalStateException("Booking is already cancelled");

    LocalDateTime slotStart = LocalDateTime.of(bookingDate, slot.startTime());
    LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);

    if (now.isAfter(slotStart.minus(cancellationDeadline)))
      throw new BookingCancellationNotAllowedException(
          "Bookings cannot be cancelled within "
              + cancellationDeadline.toHours()
              + " hours of the scheduled start time");

    this.status = BookingStatus.CANCELLED;
    this.paymentStatus = PaymentStatus.REFUNDED;
    this.cancelledAt = clock.instant();
    this.cancelReason = reason;
    this.updatedAt = clock.instant();
  }

  public void adminCancel(String reason, Clock clock) {
    if (this.status == BookingStatus.CANCELLED)
      throw new IllegalStateException("Booking is already cancelled");
    this.status = BookingStatus.CANCELLED;
    this.paymentStatus =
        this.paymentStatus == PaymentStatus.PAID ? PaymentStatus.REFUNDED : this.paymentStatus;
    this.cancelledAt = clock.instant();
    this.cancelReason = reason;
    this.updatedAt = clock.instant();
  }

  public boolean isMatchBooking() {
    return status == BookingStatus.PENDING_MATCH || status == BookingStatus.CONFIRMED;
  }

  public boolean isOwnedBy(UserId userId) {
    return this.playerId.equals(userId);
  }

  public boolean isPaid() {
    return this.paymentStatus == PaymentStatus.PAID;
  }

  public boolean isExpired(Clock clock) {
    return expiresAt != null && clock.instant().isAfter(expiresAt);
  }

  public BookingId getId() {
    return id;
  }

  public ResourceId getResourceId() {
    return resourceId;
  }

  public UserId getPlayerId() {
    return playerId;
  }

  public LocalDate getBookingDate() {
    return bookingDate;
  }

  public SlotRange getSlot() {
    return slot;
  }

  public BigDecimal getPricePaid() {
    return pricePaid;
  }

  public String getCurrency() {
    return currency;
  }

  public BookingStatus getStatus() {
    return status;
  }

  public PaymentStatus getPaymentStatus() {
    return paymentStatus;
  }

  public Instant getCancelledAt() {
    return cancelledAt;
  }

  public String getCancelReason() {
    return cancelReason;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }
}
