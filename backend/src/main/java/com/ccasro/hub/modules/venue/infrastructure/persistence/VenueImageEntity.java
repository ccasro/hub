package com.ccasro.hub.modules.venue.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "venue_image")
@Getter
@Setter
public class VenueImageEntity {

  @Id
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "venue_id", nullable = false)
  private VenueEntity venue;

  @Column(nullable = false, length = 500)
  private String url;

  @Column(name = "public_id", nullable = false, length = 200)
  private String publicId;

  @Column(name = "display_order")
  private int displayOrder;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
