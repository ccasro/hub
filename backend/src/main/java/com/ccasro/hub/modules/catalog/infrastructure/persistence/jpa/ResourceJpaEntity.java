package com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resources")
public class ResourceJpaEntity {

  @Id public UUID id;

  @Column(name = "venue_id", nullable = false)
  public UUID venueId;

  @Column(nullable = false, length = 60)
  public String name;

  @Column(columnDefinition = "text")
  public String description;

  @Column(name = "base_price_amount", nullable = false, precision = 19, scale = 4)
  public BigDecimal basePriceAmount;

  @Column(name = "base_price_currency", nullable = false, length = 3)
  public String basePriceCurrency;

  @Column(name = "primary_image_public_id")
  public String primaryImagePublicId;

  @Column(name = "primary_image_url")
  public String primaryImageUrl;

  @Column(nullable = false, length = 20)
  public String status;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  public Instant updatedAt;
}
