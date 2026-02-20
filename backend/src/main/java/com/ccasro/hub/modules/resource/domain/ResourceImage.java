package com.ccasro.hub.modules.resource.domain;

import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

final class ResourceImage {

  private final UUID id;
  private final ImageUrl imageUrl;
  private final int displayOrder;
  private final Instant createdAt;

  private ResourceImage(UUID id, ImageUrl imageUrl, int displayOrder, Instant createdAt) {
    this.id = id;
    this.imageUrl = imageUrl;
    if (displayOrder < 0) throw new IllegalArgumentException("displayOrder must be >= 0");
    this.displayOrder = displayOrder;
    this.createdAt = createdAt;
  }

  static ResourceImage create(ImageUrl imageUrl, int displayOrder, Clock clock) {
    return new ResourceImage(UUID.randomUUID(), imageUrl, displayOrder, clock.instant());
  }

  static ResourceImage reconstitute(
      UUID id, ImageUrl imageUrl, int displayOrder, Instant createdAt) {
    return new ResourceImage(id, imageUrl, displayOrder, createdAt);
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
