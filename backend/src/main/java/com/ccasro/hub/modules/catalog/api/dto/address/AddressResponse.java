package com.ccasro.hub.modules.catalog.api.dto.address;

import com.ccasro.hub.modules.catalog.domain.model.venue.Address;

public record AddressResponse(String street, String city, String postalCode, String country) {
  public static AddressResponse from(Address address) {
    return new AddressResponse(
        address.street(), address.city(), address.postalCode(), address.country());
  }
}
