package com.ccasro.hub.modules.catalog.application.query.dto;

import com.ccasro.hub.modules.catalog.api.dto.ImageResponse;
import com.ccasro.hub.modules.catalog.api.dto.MoneyResponse;
import com.ccasro.hub.modules.catalog.domain.model.media.Image;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import java.util.Comparator;
import java.util.List;

public record PublicResourceDetailDto(
    String id,
    String venueId,
    String name,
    String description,
    MoneyResponse basePricePerHour,
    String primaryImageUrl,
    List<ImageResponse> images) {
  public static PublicResourceDetailDto from(Resource r) {

    var images =
        r.images().stream()
            .sorted(
                Comparator.comparing(Image::primary).reversed().thenComparingInt(Image::position))
            .map(ImageResponse::from)
            .toList();

    return new PublicResourceDetailDto(
        r.id().toString(),
        r.venueId().toString(),
        r.name().value(),
        r.description() == null ? null : r.description().value(),
        new MoneyResponse(
            r.basePricePerHour().amount().toPlainString(),
            r.basePricePerHour().currency().getCurrencyCode()),
        r.primaryImageUrl(),
        images);
  }
}
