package com.ccasro.hub.modules.venue.infrastructure.api.dto;

import com.ccasro.hub.modules.venue.domain.VenueImageSnapshot;
import java.util.UUID;

public record VenueImageResponse(UUID id, String url, int displayOrder) {

  public static VenueImageResponse from(VenueImageSnapshot img) {
    return new VenueImageResponse(img.id(), img.url(), img.displayOrder());
  }
}
