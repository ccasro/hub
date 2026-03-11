package com.ccasro.hub.modules.booking.domain.exception;

public class PaymentNotFoundException extends RuntimeException {
  public PaymentNotFoundException(String paymentIntentId) {
    super("Payment not found: " + paymentIntentId);
  }
}
