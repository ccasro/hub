package com.ccasro.hub.modules.booking.infrastructure.api;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.exception.BookingNotFoundException;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.modules.booking.domain.ports.out.PaymentRepositoryPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.FakePaymentRequest;
import com.ccasro.hub.modules.booking.usecases.ConfirmBookingPaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dev/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dev - Payments", description = "Payment simulation (dev only)")
@ConditionalOnProperty(name = "payments.provider", havingValue = "fake", matchIfMissing = true)
public class FakePaymentController {

  private final ConfirmBookingPaymentService confirmPaymentService;
  private final PaymentRepositoryPort paymentRepository;
  private final BookingRepositoryPort bookingRepository;

  @PostMapping("/{bookingId}/confirm")
  public ResponseEntity<Void> confirmPayment(
      @PathVariable UUID bookingId, @RequestBody FakePaymentRequest request) {
    BookingId id = BookingId.of(bookingId);

    Booking booking = bookingRepository.findById(id).orElseThrow(BookingNotFoundException::new);

    if (request.amount().compareTo(booking.getPricePaid()) != 0) {
      log.warn(
          "[FAKE PAYMENT] Invalid amount" + "Expected: {} Received: {}",
          booking.getPricePaid(),
          request.amount());
      return ResponseEntity.badRequest().build();
    }

    if (!request.currency().equalsIgnoreCase(booking.getCurrency())) {
      log.warn(
          "[FAKE PAYMENT] Incorrect currency" + "Expected: {} Received: {}",
          booking.getCurrency(),
          request.currency());
      return ResponseEntity.badRequest().build();
    }

    String paymentIntentId =
        paymentRepository
            .findByBookingId(id)
            .map(Payment::getStripePaymentIntentId)
            .orElseThrow(() -> new RuntimeException("Payment not found for booking: " + bookingId));

    log.info(
        "[FAKE PAYMENT] Payment confirmed of {} {} for booking {}",
        request.amount(),
        request.currency(),
        bookingId);

    confirmPaymentService.execute(paymentIntentId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{bookingId}/fail")
  public ResponseEntity<Void> failPayment(@PathVariable UUID bookingId) {
    log.info("[DEV] Simulating payment failure for booking {}", bookingId);

    paymentRepository
        .findByBookingId(new BookingId(bookingId))
        .ifPresent(
            payment -> {
              payment.markAsFailed();
              paymentRepository.save(payment);
            });

    return ResponseEntity.ok().build();
  }
}
