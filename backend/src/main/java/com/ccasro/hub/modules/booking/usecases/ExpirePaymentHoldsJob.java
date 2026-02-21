package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingNotificationPort;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpirePaymentHoldsJob {

  private final BookingRepositoryPort bookingRepository;
  private final PaymentRepositoryPort paymentRepository;
  private final BookingNotificationPort notificationPort;
  private final UserProfileRepositoryPort userRepository;
  private final Clock clock;

  @Scheduled(fixedDelayString = "PT1M")
  @Transactional
  public void execute() {
    List<Booking> expiredHolds = bookingRepository.findExpiredHolds(clock.instant());

    if (expiredHolds.isEmpty()) return;

    log.info("Releasing {} expired payment holds", expiredHolds.size());

    expiredHolds.forEach(
        booking -> {
          try {
            booking.expireHold(clock);
            bookingRepository.save(booking);

            paymentRepository
                .findByBookingId(booking.getId())
                .ifPresent(
                    payment -> {
                      payment.markAsFailed();
                      paymentRepository.save(payment);
                    });

            log.info("Expired hold released: booking={}", booking.getId().value());

            try {
              userRepository
                  .findById(booking.getPlayerId())
                  .map(p -> p.getEmail().value())
                  .ifPresent(email -> notificationPort.notifyBookingExpired(booking, email));
            } catch (Exception e) {
              log.warn("Error sending expiration email: {}", e.getMessage());
            }

          } catch (Exception e) {
            log.error(
                "Error expiring hold for booking {}: {}", booking.getId().value(), e.getMessage());
          }
        });
  }
}
