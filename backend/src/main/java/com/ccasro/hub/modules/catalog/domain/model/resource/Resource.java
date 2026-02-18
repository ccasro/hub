package com.ccasro.hub.modules.catalog.domain.model.resource;

import com.ccasro.hub.common.domain.exception.DomainException;
import com.ccasro.hub.common.domain.model.vo.Description;
import com.ccasro.hub.common.domain.model.vo.Money;
import com.ccasro.hub.modules.catalog.domain.model.media.Image;
import com.ccasro.hub.modules.catalog.domain.model.media.ImageGallery;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import java.time.Instant;
import java.util.*;

public class Resource {

  private static final int MAX_IMAGES = 6;

  private final ResourceId id;
  private final VenueId venueId;

  private ResourceName name;
  private Description description;

  private Money basePricePerHour;

  private String primaryImagePublicId;
  private String primaryImageUrl;

  private ResourceStatus status;

  private final ImageGallery gallery;

  private final Instant createdAt;
  private Instant updatedAt;

  private Resource(
      ResourceId id,
      VenueId venueId,
      ResourceName name,
      Description description,
      Money basePricePerHour,
      ResourceStatus status,
      List<Image> images,
      Instant createdAt,
      Instant updatedAt) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.venueId = Objects.requireNonNull(venueId, "venueId is required");
    this.name = Objects.requireNonNull(name, "name is required");
    this.description = description;
    this.basePricePerHour =
        Objects.requireNonNull(basePricePerHour, "basePricePerHour is required");

    this.status = Objects.requireNonNull(status, "status is required");

    this.gallery = new ImageGallery(MAX_IMAGES, images);

    Instant now = Instant.now();
    this.createdAt = createdAt == null ? now : createdAt;
    this.updatedAt = updatedAt == null ? this.createdAt : updatedAt;

    syncPrimaryCache();
  }

  public static Resource create(
      VenueId venueId, ResourceName name, Description description, Money basePricePerHour) {
    Instant now = Instant.now();
    return new Resource(
        ResourceId.newId(),
        venueId,
        name,
        description,
        basePricePerHour,
        ResourceStatus.DRAFT,
        List.of(),
        now,
        now);
  }

  public static Resource rehydrate(
      ResourceId id,
      VenueId venueId,
      ResourceName name,
      Description description,
      Money basePricePerHour,
      ResourceStatus status,
      List<Image> images,
      Instant createdAt,
      Instant updatedAt) {
    return new Resource(
        id, venueId, name, description, basePricePerHour, status, images, createdAt, updatedAt);
  }

  // --- IMAGES (gallery) ---

  public List<Image> images() {
    return gallery.images();
  }

  public void addImage(String publicIdRaw, String urlRaw) {
    gallery.add(publicIdRaw, urlRaw);
    syncPrimaryCache();
    touch();
  }

  public void removeImageById(String imageIdRaw) {
    gallery.removeById(imageIdRaw);
    syncPrimaryCache();
    touch();
  }

  public void setPrimaryById(String imageIdRaw) {
    gallery.setPrimaryById(imageIdRaw);
    syncPrimaryCache();
    touch();
  }

  // --- DOMAIN ---

  public void activate() {
    if (status == ResourceStatus.ACTIVE) return;
    status = ResourceStatus.ACTIVE;
  }

  public void suspend() {
    if (status != ResourceStatus.ACTIVE) {
      throw new DomainException("Only ACTIVE resources can be suspended");
    }
    status = ResourceStatus.SUSPENDED;
  }

  public void rename(ResourceName newName) {
    this.name = Objects.requireNonNull(newName, "newName is required");
    touch();
  }

  public void updateDescription(Description description) {
    this.description = description;
    touch();
  }

  public void changeBasePrice(Money newPrice) {
    this.basePricePerHour = Objects.requireNonNull(newPrice, "newPrice is required");
    touch();
  }

  private void syncPrimaryCache() {
    var p = gallery.primaryOrNull();

    if (p == null) {
      primaryImagePublicId = null;
      primaryImageUrl = null;
    } else {
      primaryImagePublicId = p.publicId().value();
      primaryImageUrl = p.url().toString();
    }
  }

  private void touch() {
    this.updatedAt = Instant.now();
  }

  public ResourceStatus status() {
    return status;
  }

  public ResourceId id() {
    return id;
  }

  public VenueId venueId() {
    return venueId;
  }

  public ResourceName name() {
    return name;
  }

  public Description description() {
    return description;
  }

  public Money basePricePerHour() {
    return basePricePerHour;
  }

  public String primaryImagePublicId() {
    return primaryImagePublicId;
  }

  public String primaryImageUrl() {
    return primaryImageUrl;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
