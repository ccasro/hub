package com.ccasro.hub.modules.catalog.api.dto.venue;

import com.ccasro.hub.modules.catalog.api.dto.ImageResponse;
import com.ccasro.hub.modules.catalog.api.dto.address.AddressResponse;
import com.ccasro.hub.modules.catalog.api.dto.geolocation.GeoLocationResponse;
import com.ccasro.hub.modules.catalog.application.query.dto.ImageDto;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicVenueDetailDto;
import java.util.Comparator;
import java.util.List;

public record VenueDetailResponse(
    String id,
    String name,
    String description,
    AddressResponse address,
    GeoLocationResponse location,
    String primaryImageUrl,
    List<ImageResponse> images) {

  public static VenueDetailResponse from(PublicVenueDetailDto v) {

    AddressResponse address = null;
    if (v.address() != null) {
      address =
          new AddressResponse(
              v.address().street(),
              v.address().city(),
              v.address().postalCode(),
              v.address().country());
    }

    GeoLocationResponse location =
        v.geo() == null ? null : new GeoLocationResponse(v.geo().latitude(), v.geo().longitude());

    var images =
        v.images().stream()
            .sorted(
                Comparator.comparing(ImageDto::primary)
                    .reversed()
                    .thenComparingInt(ImageDto::position))
            .map(ImageResponse::from)
            .toList();

    return new VenueDetailResponse(
        v.id(),
        v.name(),
        v.description() == null ? null : v.description(),
        address,
        location,
        v.primaryImageUrl(),
        images);
  }
}
