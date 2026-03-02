package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.exception.BookingNotFoundException;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FakePaymentService {

  private final ConfirmBookingPaymentService confirmPaymentService;
  private final PaymentRepositoryPort paymentRepository;
  private final BookingRepositoryPort bookingRepository;

  @Transactional
  public void confirm(BookingId bookingId, BigDecimal amount, String currency) {
    Booking booking =
        bookingRepository.findById(bookingId).orElseThrow(BookingNotFoundException::new);

    if (amount.compareTo(booking.getPricePaid()) != 0) {
      log.warn(
          "[FAKE PAYMENT] Invalid amount Expected: {} Received: {}",
          booking.getPricePaid(),
          amount);
      throw new IllegalArgumentException("Invalid payment amount");
    }

    if (!currency.equalsIgnoreCase(booking.getCurrency())) {
      log.warn(
          "[FAKE PAYMENT] Incorrect currency Expected: {} Received: {}",
          booking.getCurrency(),
          currency);
      throw new IllegalArgumentException("Invalid payment currency");
    }

    String paymentIntentId =
        paymentRepository
            .findByBookingId(bookingId)
            .map(Payment::getStripePaymentIntentId)
            .orElseThrow(
                () -> new RuntimeException("Payment not found for booking: " + bookingId.value()));

    log.info(
        "[FAKE PAYMENT] Payment confirmed of {} {} for booking {}",
        amount,
        currency,
        bookingId.value());

    confirmPaymentService.execute(paymentIntentId);
  }

  @Transactional
  public void fail(BookingId bookingId) {
    log.info("[DEV] Simulating payment failure for booking {}", bookingId.value());

    paymentRepository
        .findByBookingId(bookingId)
        .ifPresent(
            payment -> {
              payment.markAsFailed();
              paymentRepository.save(payment);
            });
  }
}
