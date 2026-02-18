package com.ccasro.hub.modules.catalog.domain.model.venue;

import com.ccasro.hub.common.domain.exception.DomainException;
import com.ccasro.hub.common.domain.model.vo.Description;
import com.ccasro.hub.modules.catalog.application.policy.VenuePublishabilityPolicy;
import com.ccasro.hub.modules.catalog.domain.model.media.Image;
import com.ccasro.hub.modules.catalog.domain.model.media.ImageGallery;
import com.ccasro.hub.modules.iam.domain.UserId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Venue {

  private static final int MAX_IMAGES = 3;

  private final VenueId id;
  private final UserId ownerUserId;

  private VenueName name;
  private Description description;

  private Address address;
  private GeoLocation location;

  private String primaryImagePublicId;
  private String primaryImageUrl;

  private final ImageGallery gallery;

  private VenueStatus status;

  private final Instant createdAt;
  private Instant updatedAt;

  private Venue(
      VenueId id,
      UserId ownerUserId,
      VenueName name,
      Description description,
      Address address,
      GeoLocation location,
      List<Image> images,
      VenueStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.id = Objects.requireNonNull(id, "id is required");
    this.ownerUserId = Objects.requireNonNull(ownerUserId, "ownerUserId is required");
    this.name = Objects.requireNonNull(name, "name is required");
    this.description = description;

    this.gallery = new ImageGallery(MAX_IMAGES, images);

    this.status = Objects.requireNonNull(status, "status is required");

    Instant now = Instant.now();
    this.createdAt = createdAt == null ? now : createdAt;
    this.updatedAt = updatedAt == null ? this.createdAt : updatedAt;

    syncPrimaryCache();
  }

  public static Venue create(UserId ownerUserId, VenueName name, Description description) {
    Instant now = Instant.now();
    return new Venue(
        VenueId.newId(),
        ownerUserId,
        name,
        description,
        null,
        null,
        List.of(),
        VenueStatus.DRAFT,
        now,
        now);
  }

  public static Venue rehydrate(
      VenueId id,
      UserId ownerUserId,
      VenueName name,
      Description description,
      Address address,
      GeoLocation location,
      List<Image> images,
      VenueStatus status,
      Instant createdAt,
      Instant updatedAt) {
    return new Venue(
        id,
        ownerUserId,
        name,
        description,
        address,
        location,
        images,
        status,
        createdAt,
        updatedAt);
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

  // --- domain ---

  public boolean isOwnedBy(UserId userId) {
    return ownerUserId.equals(userId);
  }

  public void rename(VenueName newName) {
    this.name = Objects.requireNonNull(newName, "newName is required");
    touch();
  }

  public void updateDescription(Description description) {
    this.description = description;
    touch();
  }

  public void updateAddress(Address address) {
    this.address = address;
    touch();
  }

  public void updateLocation(GeoLocation location) {
    this.location = location;
    touch();
  }

  public void activate() {
    if (status == VenueStatus.ACTIVE) return;
    ensurePublishableBasics();
    status = VenueStatus.ACTIVE;
    touch();
  }

  public void suspend() {
    if (status != VenueStatus.ACTIVE) {
      throw new DomainException("Only ACTIVE venues can be suspended");
    }
    status = VenueStatus.SUSPENDED;
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

  private void ensurePublishableBasics() {
    if (primaryImageUrl == null || primaryImageUrl.isBlank()) {
      throw new DomainException("Primary image is required to activate venue");
    }
    if (address == null) {
      throw new DomainException("Address is required to activate venue");
    }
    if (location == null) {
      throw new DomainException("Location is required to activate venue");
    }
  }

  public void ensurePublishable(VenuePublishabilityPolicy policy) {
    ensurePublishableBasics();
    policy.ensurePublishable(this);
  }

  ///

  public VenueId id() {
    return id;
  }

  public UserId ownerUserId() {
    return ownerUserId;
  }

  public VenueName name() {
    return name;
  }

  public Description description() {
    return description;
  }

  public Address address() {
    return address;
  }

  public GeoLocation location() {
    return location;
  }

  public String primaryImagePublicId() {
    return primaryImagePublicId;
  }

  public String primaryImageUrl() {
    return primaryImageUrl;
  }

  public VenueStatus status() {
    return status;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
