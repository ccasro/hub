package com.ccasro.hub.modules.venue.infrastructure.api.dto;

import com.ccasro.hub.modules.venue.domain.Venue;
import java.util.List;

public final class VenueResponseMapper {

  private VenueResponseMapper() {}

  public static VenueResponse from(Venue venue) {
    return VenueResponse.from(venue);
  }

  public static List<VenueResponse> fromList(List<Venue> venues) {
    return venues.stream().map(VenueResponse::from).toList();
  }
}
