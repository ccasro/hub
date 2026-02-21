package com.ccasro.hub.modules.booking.domain.exception;

public class BookingNotFoundException extends RuntimeException {
  public BookingNotFoundException() {
    super("Booking not found");
  }
}
