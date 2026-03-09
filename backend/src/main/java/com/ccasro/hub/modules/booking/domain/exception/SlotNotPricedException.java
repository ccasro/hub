package com.ccasro.hub.modules.booking.domain.exception;

public class SlotNotPricedException extends RuntimeException {
  public SlotNotPricedException() {
    super("This slot does not have a price set");
  }
}
