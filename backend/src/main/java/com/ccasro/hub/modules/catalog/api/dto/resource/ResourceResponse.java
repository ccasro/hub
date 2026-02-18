package com.ccasro.hub.modules.catalog.api.dto.resource;

import com.ccasro.hub.modules.catalog.api.dto.MoneyResponse;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;

public record ResourceResponse(
    String id,
    String venueId,
    String name,
    String description,
    MoneyResponse basePricePerHour,
    String primaryImageUrl,
    String status) {
  public static ResourceResponse from(Resource r) {
    return new ResourceResponse(
        r.id().toString(),
        r.venueId().toString(),
        r.name().value(),
        r.description() == null ? null : r.description().value(),
        new MoneyResponse(
            r.basePricePerHour().amount().toPlainString(),
            r.basePricePerHour().currency().getCurrencyCode()),
        r.primaryImageUrl(),
        r.status().name());
  }
}
