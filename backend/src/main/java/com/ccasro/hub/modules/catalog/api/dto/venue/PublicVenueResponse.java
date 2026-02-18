package com.ccasro.hub.modules.catalog.api.dto.venue;

import com.ccasro.hub.modules.catalog.api.dto.address.AddressResponse;
import com.ccasro.hub.modules.catalog.api.dto.geolocation.GeoLocationResponse;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicVenueSummaryDto;

public record PublicVenueResponse(
    String id,
    String name,
    String description,
    String primaryImageUrl,
    AddressResponse address,
    GeoLocationResponse geo) {
  public static PublicVenueResponse from(PublicVenueSummaryDto d) {
    return new PublicVenueResponse(
        d.id().toString(),
        d.name(),
        d.description(),
        d.primaryImageUrl(),
        (d.city() == null && d.country() == null && d.street() == null && d.postalCode() == null)
            ? null
            : new AddressResponse(d.street(), d.city(), d.postalCode(), d.country()),
        (d.latitude() == null || d.longitude() == null)
            ? null
            : new GeoLocationResponse(d.latitude(), d.longitude()));
  }
}
