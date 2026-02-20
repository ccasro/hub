package com.ccasro.hub.modules.resource.domain;

import com.ccasro.hub.modules.resource.domain.exception.ResourceImageNotFoundException;
import com.ccasro.hub.modules.resource.domain.valueobjects.*;
import com.ccasro.hub.modules.venue.domain.valueobjects.*;
import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;

public class Resource {

  private final ResourceId id;
  private final VenueId venueId;
  private ResourceName name;
  private String description;
  private ResourceType type;
  private SlotDuration slotDuration;
  private final Map<DayOfWeek, DaySchedule> schedules;
  private final List<PriceRule> priceRules;
  private final List<ResourceImage> images;
  private ResourceStatus status;
  private String rejectReason;
  private final Instant createdAt;
  private Instant updatedAt;

  private Resource(
      ResourceId id,
      VenueId venueId,
      ResourceName name,
      String description,
      ResourceType type,
      SlotDuration slotDuration,
      Instant now) {
    this.id = id;
    this.venueId = venueId;
    this.name = name;
    this.description = description;
    this.type = type;
    this.slotDuration = slotDuration;
    this.schedules = new EnumMap<>(DayOfWeek.class);
    this.priceRules = new ArrayList<>();
    this.images = new ArrayList<>();
    this.status = ResourceStatus.PENDING_REVIEW;
    this.rejectReason = null;
    this.createdAt = now;
    this.updatedAt = now;
  }

  private Resource(
      ResourceId id,
      VenueId venueId,
      ResourceName name,
      String description,
      ResourceType type,
      SlotDuration slotDuration,
      Map<DayOfWeek, DaySchedule> schedules,
      List<PriceRule> priceRules,
      List<ResourceImage> images,
      ResourceStatus status,
      String rejectReason,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.venueId = venueId;
    this.name = name;
    this.description = description;
    this.type = type;
    this.slotDuration = slotDuration;

    this.schedules = new EnumMap<>(DayOfWeek.class);
    this.schedules.putAll(schedules);

    this.priceRules = new ArrayList<>(priceRules);
    this.images = new ArrayList<>(images);

    this.status = status;
    this.rejectReason = rejectReason;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Resource create(
      VenueId venueId,
      ResourceName name,
      String description,
      ResourceType type,
      SlotDuration slotDuration,
      Clock clock) {
    return new Resource(
        ResourceId.generate(), venueId, name, description, type, slotDuration, clock.instant());
  }

  public static Resource reconstitute(
      ResourceId id,
      VenueId venueId,
      ResourceName name,
      String description,
      ResourceType type,
      SlotDuration slotDuration,
      List<DaySchedule> schedules,
      List<PriceRuleReconstitutionData> priceRules,
      List<ResourceImageReconstitutionData> images,
      ResourceStatus status,
      String rejectReason,
      Instant createdAt,
      Instant updatedAt) {
    var schedulesMap = new EnumMap<DayOfWeek, DaySchedule>(DayOfWeek.class);
    schedules.forEach(s -> schedulesMap.put(s.getDayOfWeek(), s));

    var domainPriceRules =
        priceRules.stream()
            .map(
                d ->
                    PriceRule.reconstitute(
                        d.id(), d.dayType(), d.startTime(), d.endTime(), d.price(), d.currency()))
            .toList();

    var domainImages =
        images.stream()
            .map(
                d ->
                    ResourceImage.reconstitute(
                        d.id(),
                        new ImageUrl(d.url(), d.publicId()),
                        d.displayOrder(),
                        d.createdAt()))
            .toList();

    return new Resource(
        id,
        venueId,
        name,
        description,
        type,
        slotDuration,
        schedulesMap,
        domainPriceRules,
        domainImages,
        status,
        rejectReason,
        createdAt,
        updatedAt);
  }

  public List<ResourceImageSnapshot> images() {
    return images.stream()
        .map(
            i ->
                new ResourceImageSnapshot(
                    i.getId(),
                    i.getImageUrl().url(),
                    i.getImageUrl().publicId(),
                    i.getDisplayOrder(),
                    i.getCreatedAt()))
        .toList();
  }

  // ── owner ─────────────────────────────────────

  public void update(
      ResourceName name,
      String description,
      ResourceType type,
      SlotDuration slotDuration,
      Clock clock) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.slotDuration = slotDuration;
    this.updatedAt = clock.instant();

    if (status == ResourceStatus.ACTIVE) {
      this.status = ResourceStatus.PENDING_REVIEW;
    }
  }

  public void setSchedule(DayOfWeek day, LocalTime opening, LocalTime closing, Clock clock) {
    schedules.put(day, DaySchedule.create(day, opening, closing));
    this.updatedAt = clock.instant();
  }

  public void removeSchedule(DayOfWeek day, Clock clock) {
    if (!schedules.containsKey(day))
      throw new IllegalArgumentException("There is no schedule for: " + day);
    schedules.remove(day);
    this.updatedAt = clock.instant();
  }

  public void addPriceRule(
      DayType dayType,
      LocalTime startTime,
      LocalTime endTime,
      BigDecimal price,
      String currency,
      Clock clock) {
    priceRules.add(PriceRule.create(dayType, startTime, endTime, price, currency));
    this.updatedAt = clock.instant();
  }

  public void removePriceRule(UUID priceRuleId, Clock clock) {
    boolean removed = priceRules.removeIf(r -> r.getId().equals(priceRuleId));
    if (!removed) throw new IllegalArgumentException("PriceRule not found: " + priceRuleId);
    this.updatedAt = clock.instant();
  }

  public void addImage(ImageUrl imageUrl, Clock clock) {
    images.add(ResourceImage.create(imageUrl, images.size(), clock));
    this.updatedAt = clock.instant();
  }

  public void removeImage(UUID imageId, Clock clock) {
    boolean removed = images.removeIf(img -> img.getId().equals(imageId));
    if (!removed) throw new ResourceImageNotFoundException();
    this.updatedAt = clock.instant();
  }

  public void suspend(Clock clock) {
    if (this.status != ResourceStatus.ACTIVE)
      throw new IllegalStateException("Only can be suspended an active resource");
    this.status = ResourceStatus.SUSPENDED;
    this.updatedAt = clock.instant();
  }

  public void reactivate(Clock clock) {
    if (this.status != ResourceStatus.SUSPENDED)
      throw new IllegalStateException("Only can be reactivated a suspended resource");
    this.status = ResourceStatus.ACTIVE;
    this.updatedAt = clock.instant();
  }

  // ── admin ─────────────────────────────────────

  public void approve(Clock clock) {
    if (this.status != ResourceStatus.PENDING_REVIEW)
      throw new IllegalStateException("The resource is not pending review");
    this.status = ResourceStatus.ACTIVE;
    this.rejectReason = null;
    this.updatedAt = clock.instant();
  }

  public void reject(String reason, Clock clock) {
    if (reason == null || reason.isBlank())
      throw new IllegalArgumentException("The reject reason is mandatory");
    this.status = ResourceStatus.REJECTED;
    this.rejectReason = reason;
    this.updatedAt = clock.instant();
  }

  public void adminSuspend(String reason, Clock clock) {
    if (reason == null || reason.isBlank())
      throw new IllegalArgumentException("The suspend reason is mandatory");
    this.status = ResourceStatus.SUSPENDED;
    this.rejectReason = reason;
    this.updatedAt = clock.instant();
  }

  public List<SlotRange> generateSlotsForDay(DayOfWeek day) {
    DaySchedule schedule = schedules.get(day);
    if (schedule == null) return Collections.emptyList();
    return schedule.generateSlots(slotDuration);
  }

  public Optional<BigDecimal> getPriceForSlot(DayOfWeek day, LocalTime startTime) {
    return priceRules.stream()
        .filter(rule -> rule.appliesTo(day, startTime))
        .max(
            Comparator.comparing(
                rule -> {
                  boolean isSpecific = rule.getDayType().name().equals(day.name());
                  return isSpecific ? 1 : 0;
                }))
        .map(PriceRule::getPrice);
  }

  public boolean isAvailableOn(DayOfWeek day) {
    return schedules.containsKey(day) && status == ResourceStatus.ACTIVE;
  }

  public boolean isOwnedByVenue(VenueId venueId) {
    return this.venueId.equals(venueId);
  }

  // ── Getters ──────────────────────────────────────────────────

  public ResourceId getId() {
    return id;
  }

  public VenueId getVenueId() {
    return venueId;
  }

  public ResourceName getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public ResourceType getType() {
    return type;
  }

  public SlotDuration getSlotDuration() {
    return slotDuration;
  }

  public Map<DayOfWeek, DaySchedule> getSchedules() {
    return Collections.unmodifiableMap(schedules);
  }

  public List<PriceRule> getPriceRules() {
    return Collections.unmodifiableList(priceRules);
  }

  public List<ResourceImageSnapshot> getImages() {
    return images();
  }

  public ResourceStatus getStatus() {
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
