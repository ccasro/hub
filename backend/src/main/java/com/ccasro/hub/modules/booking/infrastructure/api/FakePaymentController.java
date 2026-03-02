package com.ccasro.hub.modules.booking.infrastructure.api;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.FakePaymentRequest;
import com.ccasro.hub.modules.booking.usecases.FakePaymentService;
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

  private final FakePaymentService fakePaymentService;

  @PostMapping("/{bookingId}/confirm")
  public ResponseEntity<Void> confirmPayment(
      @PathVariable UUID bookingId, @RequestBody FakePaymentRequest request) {
    fakePaymentService.confirm(BookingId.of(bookingId), request.amount(), request.currency());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{bookingId}/fail")
  public ResponseEntity<Void> failPayment(@PathVariable UUID bookingId) {
    fakePaymentService.fail(BookingId.of(bookingId));
    return ResponseEntity.ok().build();
  }
}
