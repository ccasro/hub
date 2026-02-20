package com.ccasro.hub.modules.venue.domain;

import com.ccasro.hub.modules.venue.domain.exception.VenueImageNotFoundException;
import com.ccasro.hub.modules.venue.domain.valueobjects.*;
import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Venue {

  private final VenueId id;
  private final UserId ownerId;
  private VenueName name;
  private String description;
  private Address address;
  private Coordinates coordinates;
  private final List<VenueImage> images;
  private VenueStatus status;
  private String rejectReason;
  private final Instant createdAt;
  private Instant updatedAt;

  private Venue(
      VenueId id,
      UserId ownerId,
      VenueName name,
      String description,
      Address address,
      Coordinates coordinates,
      Instant now) {
    this.id = id;
    this.ownerId = ownerId;
    this.name = name;
    this.description = description;
    this.address = address;
    this.coordinates = coordinates;
    this.images = new ArrayList<>();
    this.status = VenueStatus.PENDING_REVIEW;
    this.rejectReason = null;
    this.createdAt = now;
    this.updatedAt = now;
  }

  private Venue(
      VenueId id,
      UserId ownerId,
      VenueName name,
      String description,
      Address address,
      Coordinates coordinates,
      List<VenueImage> images,
      VenueStatus status,
      String rejectReason,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.ownerId = ownerId;
    this.name = name;
    this.description = description;
    this.address = address;
    this.coordinates = coordinates;
    this.images = new ArrayList<>(images);
    this.status = status;
    this.rejectReason = rejectReason;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ── Factory methods ──────────────────────────────────────────

  public static Venue create(
      UserId ownerId,
      VenueName name,
      String description,
      Address address,
      Coordinates coordinates,
      Clock clock) {
    return new Venue(
        VenueId.generate(), ownerId, name, description, address, coordinates, clock.instant());
  }

  public static Venue reconstitute(
      VenueId id,
      UserId ownerId,
      VenueName name,
      String description,
      Address address,
      Coordinates coordinates,
      List<VenueImageReconstitutionData> images,
      VenueStatus status,
      String rejectReason,
      Instant createdAt,
      Instant updatedAt) {
    var domainImages =
        images.stream()
            .map(
                d ->
                    VenueImage.reconstitute(
                        d.id(),
                        new ImageUrl(d.url(), d.publicId()),
                        d.displayOrder(),
                        d.createdAt()))
            .toList();

    return new Venue(
        id,
        ownerId,
        name,
        description,
        address,
        coordinates,
        domainImages,
        status,
        rejectReason,
        createdAt,
        updatedAt);
  }

  public List<VenueImageSnapshot> images() {
    return images.stream()
        .map(
            i ->
                new VenueImageSnapshot(
                    i.getId(),
                    i.getImageUrl().url(),
                    i.getImageUrl().publicId(),
                    i.getDisplayOrder(),
                    i.getCreatedAt()))
        .toList();
  }

  public void update(
      VenueName name, String description, Address address, Coordinates coordinates, Clock clock) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.coordinates = coordinates;
    this.updatedAt = clock.instant();

    if (status == VenueStatus.ACTIVE) {
      this.status = VenueStatus.PENDING_REVIEW;
    }
  }

  public void suspend(Clock clock) {
    if (this.status != VenueStatus.ACTIVE)
      throw new IllegalStateException("Only one active venue can be suspended");
    this.status = VenueStatus.SUSPENDED;
    this.updatedAt = clock.instant();
  }

  public void reactivate(Clock clock) {
    if (this.status != VenueStatus.SUSPENDED)
      throw new IllegalStateException("Only a suspended venue can be reactivated");
    this.status = VenueStatus.ACTIVE;
    this.updatedAt = clock.instant();
  }

  public void addImage(ImageUrl imageUrl, Clock clock) {
    images.add(VenueImage.create(imageUrl, images.size(), clock));
    this.updatedAt = clock.instant();
  }

  public void removeImage(UUID imageId, Clock clock) {
    boolean removed = images.removeIf(img -> img.getId().equals(imageId));
    if (!removed) throw new VenueImageNotFoundException();
    this.updatedAt = clock.instant();
  }

  // ── Comportamiento admin ─────────────────────────────────────

  public void approve(Clock clock) {
    if (this.status != VenueStatus.PENDING_REVIEW)
      throw new IllegalStateException("The venue is not pending review");
    this.status = VenueStatus.ACTIVE;
    this.rejectReason = null;
    this.updatedAt = clock.instant();
  }

  public void reject(String reason, Clock clock) {
    if (reason == null || reason.isBlank())
      throw new IllegalArgumentException("The reason for rejection is mandatory");
    this.status = VenueStatus.REJECTED;
    this.rejectReason = reason;
    this.updatedAt = clock.instant();
  }

  public void adminSuspend(String reason, Clock clock) {
    if (reason == null || reason.isBlank())
      throw new IllegalArgumentException("The reason for suspension is mandatory");
    this.status = VenueStatus.SUSPENDED;
    this.rejectReason = reason;
    this.updatedAt = clock.instant();
  }

  // ── Helpers ──────────────────────────────────────────────────

  public boolean isOwnedBy(UserId userId) {
    return this.ownerId.equals(userId);
  }

  public boolean isPubliclyVisible() {
    return this.status == VenueStatus.ACTIVE;
  }

  // ── Getters ──────────────────────────────────────────────────

  public VenueId getId() {
    return id;
  }

  public UserId getOwnerId() {
    return ownerId;
  }

  public VenueName getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Address getAddress() {
    return address;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public List<VenueImageSnapshot> getImages() {
    return images();
  }

  public VenueStatus getStatus() {
    return status;
  }

  public String getRejectReason() {
    return rejectReason;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
