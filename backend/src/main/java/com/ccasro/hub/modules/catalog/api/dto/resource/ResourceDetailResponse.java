package com.ccasro.hub.modules.catalog.api.dto.resource;

import com.ccasro.hub.modules.catalog.api.dto.ImageResponse;
import com.ccasro.hub.modules.catalog.api.dto.MoneyResponse;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicResourceDetailDto;
import java.util.Comparator;
import java.util.List;

public record ResourceDetailResponse(
    String id,
    String venueId,
    String name,
    String description,
    MoneyResponse basePricePerHour,
    String primaryImageUrl,
    List<ImageResponse> images) {

  public static ResourceDetailResponse from(PublicResourceDetailDto r) {

    var images =
        r.images().stream()
            .sorted(
                Comparator.comparing(ImageResponse::primary)
                    .reversed()
                    .thenComparingInt(ImageResponse::position))
            .toList();

    return new ResourceDetailResponse(
        r.id(),
        r.venueId(),
        r.name(),
        r.description() == null ? null : r.description(),
        new MoneyResponse(r.basePricePerHour().amount(), r.basePricePerHour().currency()),
        r.primaryImageUrl(),
        images);
  }
}
