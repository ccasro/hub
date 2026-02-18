package com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa;

import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.embeddable.AddressEmbeddable;
import com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.embeddable.GeoLocationEmbeddable;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "venues")
public class VenueJpaEntity {

  @Id public UUID id;

  @Column(name = "owner_user_id", nullable = false)
  public UUID ownerUserId;

  @Column(nullable = false, length = 80)
  public String name;

  @Column(columnDefinition = "text")
  public String description;

  @Column(name = "primary_image_public_id")
  public String primaryImagePublicId;

  @Column(name = "primary_image_url")
  public String primaryImageUrl;

  @Column(nullable = false, length = 20)
  public String status;

  @Embedded public AddressEmbeddable address;

  @Embedded public GeoLocationEmbeddable geo;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  public Instant updatedAt;
}
