package com.ccasro.hub.modules.catalog.api.dto.venue;

import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;

public record VenueResponse(
    String id, String name, String description, String primaryImageUrl, String status) {
  public static VenueResponse from(Venue v) {
    return new VenueResponse(
        v.id().toString(),
        v.name().value(),
        v.description() == null ? null : v.description().value(),
        v.primaryImageUrl(),
        v.status().name());
  }
}
