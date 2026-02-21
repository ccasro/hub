package com.ccasro.hub.modules.booking.domain.exception;

public class BookingCancellationNotAllowedException extends RuntimeException {
  public BookingCancellationNotAllowedException(String message) {
    super(message);
  }
}
