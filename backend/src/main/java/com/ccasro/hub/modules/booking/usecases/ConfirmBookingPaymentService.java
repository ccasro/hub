package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.events.BookingConfirmedEvent;
import com.ccasro.hub.modules.booking.domain.exception.BookingNotFoundException;
import com.ccasro.hub.modules.booking.domain.exception.PaymentNotFoundException;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentStatus;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import java.time.Clock;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmBookingPaymentService {

  private final BookingRepositoryPort bookingRepository;
  private final PaymentRepositoryPort paymentRepository;
  private final UserProfileRepositoryPort userRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  @Transactional
  public void execute(String paymentIntentId) {
    Payment payment =
        paymentRepository
            .findByStripePaymentIntentId(paymentIntentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentIntentId));

    if (payment.getStatus() == PaymentStatus.PAID) {
      log.info("Webhook already processed for paymentIntent {}, skipping", paymentIntentId);
      return;
    }

    Booking booking =
        bookingRepository
            .findById(payment.getBookingId())
            .orElseThrow(BookingNotFoundException::new);

    booking.confirmPayment(clock);
    payment.markAsPaid(clock);

    bookingRepository.save(booking);
    paymentRepository.save(payment);

    log.info("Paid confirmed for booking: {}", booking.getId().value());

    String email =
        userRepository.findEmailsByIds(Set.of(booking.getPlayerId())).get(booking.getPlayerId());
    if (email != null) {
      eventPublisher.publishEvent(new BookingConfirmedEvent(booking.getId().value(), email));
    }
  }
}
