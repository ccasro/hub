package com.ccasro.hub.modules.venue.domain.exception;

public class VenueImageNotFoundException extends RuntimeException {
  public VenueImageNotFoundException() {
    super("Venue image not found");
  }
}
