package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.events.BookingConfirmedEvent;
import com.ccasro.hub.modules.booking.domain.exception.BookingNotFoundException;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import java.time.Clock;
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
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentIntentId));

    Booking booking =
        bookingRepository
            .findById(payment.getBookingId())
            .orElseThrow(BookingNotFoundException::new);

    booking.confirmPayment(clock);
    payment.markAsPaid(clock);

    bookingRepository.save(booking);
    paymentRepository.save(payment);

    log.info("Paid confirmed for booking: {}", booking.getId().value());

    userRepository
        .findById(booking.getPlayerId())
        .map(p -> p.getEmail().value())
        .ifPresent(
            email ->
                eventPublisher.publishEvent(
                    new BookingConfirmedEvent(booking.getId().value(), email)));
  }
}
