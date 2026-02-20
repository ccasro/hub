package com.ccasro.hub.modules.venue.domain;

import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

final class VenueImage {

  private final UUID id;
  private final ImageUrl imageUrl;
  private final int displayOrder;
  private final Instant createdAt;

  private VenueImage(UUID id, ImageUrl imageUrl, int displayOrder, Instant createdAt) {
    this.id = Objects.requireNonNull(id);
    this.imageUrl = Objects.requireNonNull(imageUrl);
    if (displayOrder < 0) throw new IllegalArgumentException("displayOrder must be >= 0");
    this.displayOrder = displayOrder;
    this.createdAt = Objects.requireNonNull(createdAt);
  }

  static VenueImage create(ImageUrl imageUrl, int displayOrder, Clock clock) {
    return new VenueImage(UUID.randomUUID(), imageUrl, displayOrder, clock.instant());
  }

  static VenueImage reconstitute(UUID id, ImageUrl imageUrl, int displayOrder, Instant createdAt) {
    return new VenueImage(id, imageUrl, displayOrder, createdAt);
  }

  UUID getId() {
    return id;
  }

  ImageUrl getImageUrl() {
    return imageUrl;
  }

  int getDisplayOrder() {
    return displayOrder;
  }

  Instant getCreatedAt() {
    return createdAt;
  }
}
