package com.ccasro.hub.modules.catalog.application.query.dto;

import com.ccasro.hub.modules.catalog.domain.model.venue.Address;

public record AddressDto(String street, String city, String postalCode, String country) {
  public static AddressDto from(Address a) {
    return new AddressDto(a.street(), a.city(), a.postalCode(), a.country());
  }
}
