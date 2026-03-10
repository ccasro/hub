package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingStatus;
import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentStatus;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchPlayerPaymentService {

  private final BookingRepositoryPort bookingRepository;
  private final PaymentPort paymentPort;
  private final PaymentRepositoryPort paymentRepository;
  private final UserProfileRepositoryPort userRepository;
  private final Clock clock;

  /**
   * Creates a payment record for a player joining a match. The amount is taken from
   * matchRequest.getPricePerPlayer().
   */
  public void createPaymentForPlayer(MatchRequest matchRequest, UserId playerId) {
    if (matchRequest.getPricePerPlayer() == null) return;

    Optional<Booking> booking = findMatchBooking(matchRequest);
    if (booking.isEmpty()) {
      log.warn("No PENDING_MATCH booking found for match {}", matchRequest.getId().value());
      return;
    }

    if (paymentRepository.findByBookingIdAndPlayerId(booking.get().getId(), playerId).isPresent()) {
      log.warn(
          "Payment already exists for player {} on booking {}, skipping duplicate charge",
          playerId.value(),
          booking.get().getId().value());
      return;
    }

    String playerEmail = userRepository.findEmailsByIds(Set.of(playerId)).get(playerId);

    PaymentPort.PaymentIntent intent =
        paymentPort.createPaymentIntent(
            matchRequest.getPricePerPlayer(),
            booking.get().getCurrency(),
            booking.get().getId(),
            playerEmail);

    Payment payment =
        Payment.createForPlayer(
            booking.get().getId(),
            playerId,
            intent.paymentIntentId(),
            matchRequest.getPricePerPlayer(),
            booking.get().getCurrency(),
            clock);

    payment.markAsPaid(clock);
    paymentRepository.save(payment);

    log.info(
        "Player payment charged for player {} on match {}",
        playerId.value(),
        matchRequest.getId().value());
  }

  /**
   * Refunds the payment of a player who is leaving a match. If the payment was never collected
   * (still PENDING), marks it as FAILED. Already-settled payments (REFUNDED/FAILED) are skipped.
   */
  public void refundPlayerPayment(MatchRequest matchRequest, UserId playerId) {
    Optional<Booking> booking = findMatchBooking(matchRequest);
    if (booking.isEmpty()) return;

    paymentRepository
        .findByBookingIdAndPlayerId(booking.get().getId(), playerId)
        .ifPresentOrElse(
            payment -> {
              if (payment.getStatus() == PaymentStatus.REFUNDED
                  || payment.getStatus() == PaymentStatus.FAILED) {
                log.info(
                    "Payment for player {} on match {} already settled ({}), skipping",
                    playerId.value(),
                    matchRequest.getId().value(),
                    payment.getStatus());
                return;
              }
              if (payment.getStatus() == PaymentStatus.PAID) {
                paymentPort.refund(payment.getStripePaymentIntentId());
                payment.markAsRefunded(clock);
              } else {
                payment.markAsFailed();
              }
              paymentRepository.save(payment);
            },
            () ->
                log.warn(
                    "No payment found for player {} on match {}",
                    playerId.value(),
                    matchRequest.getId().value()));
  }

  /** Reverts a CONFIRMED booking back to PENDING_MATCH when a player leaves a full match. */
  public void revertBookingToPendingMatch(MatchRequest matchRequest) {
    findMatchBooking(matchRequest)
        .ifPresentOrElse(
            booking -> {
              booking.revertToPendingMatch(clock);
              bookingRepository.save(booking);
            },
            () ->
                log.warn(
                    "No CONFIRMED booking found to revert for match {}",
                    matchRequest.getId().value()));
  }

  /**
   * Cancels the PENDING_MATCH booking associated with this match. Safe to call even if no booking
   * is found (logs a warning).
   */
  public void cancelMatchBooking(MatchRequest matchRequest) {
    findMatchBooking(matchRequest)
        .ifPresentOrElse(
            booking -> {
              booking.cancelMatch(clock);
              bookingRepository.save(booking);
            },
            () ->
                log.warn(
                    "No PENDING_MATCH booking found to cancel for match {}",
                    matchRequest.getId().value()));
  }

  private Optional<Booking> findMatchBooking(MatchRequest matchRequest) {
    return bookingRepository
        .findByResourceIdAndDate(matchRequest.getResourceId(), matchRequest.getBookingDate())
        .stream()
        .filter(
            b ->
                b.getStatus() == BookingStatus.PENDING_MATCH
                    || b.getStatus() == BookingStatus.CONFIRMED)
        .filter(b -> b.getSlot().startTime().equals(matchRequest.getStartTime()))
        .findFirst();
  }
}
