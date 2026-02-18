package com.ccasro.hub.modules.catalog.api.dto.venue;

import com.ccasro.hub.modules.catalog.api.dto.address.AddressRequest;
import com.ccasro.hub.modules.catalog.api.dto.geolocation.GeoLocationRequest;
import com.ccasro.hub.modules.catalog.application.command.AddressCommand;
import com.ccasro.hub.modules.catalog.application.command.GeoLocationCommand;
import com.ccasro.hub.modules.catalog.application.command.PatchVenueDetailsCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record PatchVenueRequest(
    @Size(max = 80) String name,
    @Size(max = 2000) String description,
    @Valid AddressRequest address,
    @Valid GeoLocationRequest location) {

  public PatchVenueDetailsCommand toCommand() {
    return new PatchVenueDetailsCommand(
        name,
        description,
        address == null
            ? null
            : new AddressCommand(
                address.street(), address.city(), address.postalCode(), address.country()),
        location == null
            ? null
            : new GeoLocationCommand(location.latitude(), location.longitude()));
  }
}
