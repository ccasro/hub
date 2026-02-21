package com.ccasro.hub.modules.booking.infrastructure.payment;

import com.ccasro.hub.modules.booking.domain.ports.out.PaymentPort;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "payments.provider", havingValue = "fake", matchIfMissing = true)
public class FakePaymentAdapter implements PaymentPort {

  @Override
  public PaymentIntent createPaymentIntent(
      BigDecimal amount, String currency, BookingId bookingId, String customerEmail) {
    String fakeIntentId = "pi_fake_" + UUID.randomUUID().toString().replace("-", "");
    String fakeClientSecret = fakeIntentId + "_secret_fake";

    log.info("[FAKE PAYMENT] Created PaymentIntent {} for {} {}", fakeIntentId, amount, currency);

    return new PaymentIntent(fakeIntentId, fakeClientSecret);
  }

  @Override
  public void refund(String paymentIntentId) {
    log.info("[FAKE PAYMENT] Simulated Refund for {}", paymentIntentId);
  }
}
