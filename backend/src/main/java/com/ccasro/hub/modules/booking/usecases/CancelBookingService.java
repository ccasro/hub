package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.application.dto.CancelBookingCommand;
import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.exception.BookingNotFoundException;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingNotificationPort;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelBookingService {

  private final BookingRepositoryPort bookingRepository;
  private final PaymentRepositoryPort paymentRepository;
  private final PaymentPort paymentPort;
  private final BookingNotificationPort notificationPort;
  private final UserProfileRepositoryPort userRepository;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public Booking execute(CancelBookingCommand cmd) {
    Booking booking =
        bookingRepository.findById(cmd.bookingId()).orElseThrow(BookingNotFoundException::new);

    if (!booking.isOwnedBy(currentUser.getUserId()))
      throw new AccessDeniedException("You are not the owner of this booking");

    if (booking.isPaid()) {
      paymentRepository
          .findByBookingId(booking.getId())
          .ifPresent(
              payment -> {
                paymentPort.refund(payment.getStripePaymentIntentId());
                log.info("Refund processed for booking: {}", booking.getId().value());
              });
    }

    booking.cancel(cmd.reason(), clock);
    Booking saved = bookingRepository.save(booking);

    try {
      userRepository
          .findById(currentUser.getUserId())
          .map(p -> p.getEmail().value())
          .ifPresent(playerEmail -> notificationPort.notifyBookingCancelled(saved, playerEmail));
    } catch (Exception e) {
      log.warn("Failed to send cancellation email: {}", e.getMessage());
    }

    return saved;
  }
}
