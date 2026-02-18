package com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "venue_images")
public class VenueImageJpaEntity {

  @Id public UUID id;

  @Column(name = "venue_id", nullable = false)
  public UUID venueId;

  @Column(name = "public_id", nullable = false)
  public String publicId;

  @Column(nullable = false)
  public String url;

  @Column(nullable = false)
  public int position;

  @Column(name = "is_primary", nullable = false)
  public boolean primary;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;
}
