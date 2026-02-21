package com.ccasro.hub.modules.booking.domain.exception;

public class SlotNotAvailableException extends RuntimeException {
  public SlotNotAvailableException() {
    super("Slot selected is not available");
  }
}
