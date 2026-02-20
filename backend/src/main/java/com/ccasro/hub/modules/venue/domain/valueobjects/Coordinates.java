package com.ccasro.hub.modules.venue.domain.valueobjects;

public record Coordinates(double latitude, double longitude) {

  public Coordinates {
    if (latitude < -90 || latitude > 90) throw new IllegalArgumentException("Invalid latitude");

    if (longitude < -180 || longitude > 180)
      throw new IllegalArgumentException("Invalid longitude");
  }

  public String toWkt() {
    return "POINT(" + longitude + " " + latitude + ")";
  }
}
