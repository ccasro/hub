package com.ccasro.hub.modules.venue.domain.exception;

public class VenueNotFoundException extends RuntimeException {
  public VenueNotFoundException() {
    super("Venue not found");
  }
}
