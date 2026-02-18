package com.ccasro.hub.modules.catalog.api.dto.resource;

import com.ccasro.hub.modules.catalog.api.dto.MoneyResponse;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicResourceSummaryDto;

public record ResourceSummaryResponse(
    String id,
    String venueId,
    String name,
    String description,
    MoneyResponse basePricePerHour,
    String primaryImageUrl) {
  public static ResourceSummaryResponse from(PublicResourceSummaryDto s) {
    return new ResourceSummaryResponse(
        s.id().toString(),
        s.venueId().toString(),
        s.name(),
        s.description(),
        new MoneyResponse(s.basePriceAmount().toPlainString(), s.basePriceCurrency()),
        s.primaryImageUrl());
  }
}
