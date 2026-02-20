package com.ccasro.hub.modules.venue.domain.valueobjects;

public record Address(String street, String city, String country, String postalCode) {
  public Address {
    if (street == null || street.isBlank()) throw new IllegalArgumentException("Street required");
    if (city == null || city.isBlank()) throw new IllegalArgumentException("City required");
    if (country == null || country.isBlank())
      throw new IllegalArgumentException("Country required");
    street = street.trim();
    city = city.trim();
    country = country.trim();
  }
}
