package com.ccasro.hub.modules.catalog.api.dto.venue;

import com.ccasro.hub.modules.catalog.application.query.dto.VenueSummaryDto;

public record MyVenueResponse(
    String id, String name, String description, String primaryImageUrl, String status) {
  public static MyVenueResponse from(VenueSummaryDto s) {
    return new MyVenueResponse(
        s.id().toString(), s.name(), s.description(), s.primaryImageUrl(), s.status().name());
  }
}
