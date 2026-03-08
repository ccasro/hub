package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.events.BookingExpiredEvent;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpirePaymentHoldsJob {

  private final BookingRepositoryPort bookingRepository;
  private final PaymentRepositoryPort paymentRepository;
  private final UserProfileRepositoryPort userRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  @Scheduled(fixedDelayString = "PT1M")
  @Transactional
  public void execute() {
    List<Booking> expiredHolds = bookingRepository.findExpiredHolds(clock.instant());

    if (expiredHolds.isEmpty()) return;

    log.info("Releasing {} expired payment holds", expiredHolds.size());

    Set<BookingId> bookingIds =
        expiredHolds.stream().map(Booking::getId).collect(Collectors.toSet());
    Set<UserId> playerIds =
        expiredHolds.stream().map(Booking::getPlayerId).collect(Collectors.toSet());

    Map<BookingId, Payment> payments = paymentRepository.findByBookingIds(bookingIds);
    Map<UserId, String> emails = userRepository.findEmailsByIds(playerIds);

    expiredHolds.forEach(
        booking -> {
          try {
            booking.expireHold(clock);
            bookingRepository.save(booking);

            Payment payment = payments.get(booking.getId());
            if (payment != null) {
              payment.markAsFailed();
              paymentRepository.save(payment);
            }

            log.info("Expired hold released: booking={}", booking.getId().value());

            String email = emails.get(booking.getPlayerId());
            if (email != null) {
              eventPublisher.publishEvent(new BookingExpiredEvent(booking.getId().value(), email));
            }

          } catch (Exception e) {
            log.error(
                "Error expiring hold for booking {}: {}", booking.getId().value(), e.getMessage());
          }
        });
  }
}
