package com.ccasro.hub.modules.venue.infrastructure.persistence;

import com.ccasro.hub.modules.venue.domain.valueobjects.VenueStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "venue")
@Getter
@Setter
public class VenueEntity {

  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "owner_id", nullable = false, columnDefinition = "uuid")
  private UUID ownerId;

  @Column(nullable = false, length = 150)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  private String street;
  private String city;
  private String country;

  @Column(name = "postal_code", length = 20)
  private String postalCode;

  @Column(columnDefinition = "geography(Point,4326)")
  private Point location;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private VenueStatus status;

  @Column(name = "reject_reason", columnDefinition = "TEXT")
  private String rejectReason;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("displayOrder ASC")
  private List<VenueImageEntity> images = new ArrayList<>();
}
