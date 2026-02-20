package com.ccasro.hub.modules.venue.infrastructure.api.dto;

import com.ccasro.hub.modules.venue.domain.Venue;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record VenueResponse(
    UUID id,
    UUID ownerId,
    String name,
    String description,
    String street,
    String city,
    String country,
    String postalCode,
    double latitude,
    double longitude,
    String status,
    String rejectReason,
    List<VenueImageResponse> images,
    Instant createdAt,
    Instant updatedAt) {
  public static VenueResponse from(Venue v) {
    return new VenueResponse(
        v.getId().value(),
        v.getOwnerId().value(),
        v.getName().value(),
        v.getDescription(),
        v.getAddress() != null ? v.getAddress().street() : null,
        v.getAddress() != null ? v.getAddress().city() : null,
        v.getAddress() != null ? v.getAddress().country() : null,
        v.getAddress() != null ? v.getAddress().postalCode() : null,
        v.getCoordinates() != null ? v.getCoordinates().latitude() : null,
        v.getCoordinates() != null ? v.getCoordinates().longitude() : null,
        v.getStatus().name(),
        v.getRejectReason(),
        v.getImages().stream().map(VenueImageResponse::from).toList(),
        v.getCreatedAt(),
        v.getUpdatedAt());
  }
}
