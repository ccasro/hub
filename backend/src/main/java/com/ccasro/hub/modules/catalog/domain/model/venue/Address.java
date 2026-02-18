package com.ccasro.hub.modules.catalog.domain.model.venue;

public record Address(String street, String city, String postalCode, String country) {
  public Address {
    street = norm(street);
    city = norm(city);
    postalCode = norm(postalCode);
    country = norm(country);
  }

  private static String norm(String s) {
    if (s == null) return null;
    var v = s.trim();
    return v.isBlank() ? null : v;
  }
}
