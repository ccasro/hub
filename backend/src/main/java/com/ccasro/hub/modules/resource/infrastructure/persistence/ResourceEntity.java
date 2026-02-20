package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceStatus;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resource")
@Getter
@Setter
public class ResourceEntity {

  @Id
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "venue_id", nullable = false, columnDefinition = "uuid")
  private UUID venueId;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "resource_type", nullable = false, length = 30)
  private ResourceType type;

  @Column(name = "slot_duration", nullable = false)
  private int slotDuration;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private ResourceStatus status;

  @Column(name = "reject_reason", columnDefinition = "TEXT")
  private String rejectReason;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ResourceScheduleEntity> schedules = new ArrayList<>();

  @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ResourcePriceRuleEntity> priceRules = new ArrayList<>();

  @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("displayOrder ASC")
  private List<ResourceImageEntity> images = new ArrayList<>();
}
