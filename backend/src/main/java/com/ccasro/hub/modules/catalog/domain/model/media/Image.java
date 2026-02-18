package com.ccasro.hub.modules.catalog.domain.model.media;

import java.time.Instant;
import java.util.Objects;

public class Image {

  private final ImageId id;
  private final MediaPublicId publicId;
  private final MediaUrl url;

  private ImagePosition position;
  private boolean primary;

  private final Instant createdAt;

  public Image(
      ImageId id,
      MediaPublicId publicId,
      MediaUrl url,
      ImagePosition position,
      boolean primary,
      Instant createdAt) {
    this.id = Objects.requireNonNull(id);
    this.publicId = Objects.requireNonNull(publicId);
    this.url = Objects.requireNonNull(url);
    this.position = Objects.requireNonNull(position);
    this.primary = primary;
    this.createdAt = createdAt == null ? Instant.now() : createdAt;
  }

  public static Image create(MediaPublicId publicId, MediaUrl url, int position, boolean primary) {
    return new Image(
        ImageId.newId(), publicId, url, new ImagePosition(position), primary, Instant.now());
  }

  public void setPrimary(boolean primary) {
    this.primary = primary;
  }

  public void setPosition(int position) {
    this.position = new ImagePosition(position);
  }

  public ImageId id() {
    return id;
  }

  public MediaPublicId publicId() {
    return publicId;
  }

  public MediaUrl url() {
    return url;
  }

  public int position() {
    return position.value();
  }

  public boolean primary() {
    return primary;
  }

  public Instant createdAt() {
    return createdAt;
  }
}
