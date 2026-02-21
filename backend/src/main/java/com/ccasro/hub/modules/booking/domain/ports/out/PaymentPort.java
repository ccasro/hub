package com.ccasro.hub.modules.booking.domain.ports.out;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import java.math.BigDecimal;

public interface PaymentPort {

  PaymentIntent createPaymentIntent(
      BigDecimal amount, String currency, BookingId bookingId, String customerEmail);

  void refund(String paymentIntentId);

  record PaymentIntent(String paymentIntentId, String clientSecret) {}
}
