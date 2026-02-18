package com.ccasro.hub.modules.catalog.application.query.dto;

import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import java.util.List;

public record PublicVenueDetailDto(
    String id,
    String name,
    String description,
    String primaryImageUrl,
    AddressDto address,
    GeoLocationDto geo,
    List<ImageDto> images) {
  public static PublicVenueDetailDto from(Venue v) {
    return new PublicVenueDetailDto(
        v.id().toString(),
        v.name().value(),
        v.description() == null ? null : v.description().value(),
        v.primaryImageUrl(),
        v.address() == null ? null : AddressDto.from(v.address()),
        v.location() == null ? null : GeoLocationDto.from(v.location()),
        v.images().stream().map(ImageDto::from).toList());
  }
}
