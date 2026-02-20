package com.ccasro.hub.modules.resource.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resource_image")
@Getter
@Setter
public class ResourceImageEntity {

  @Id
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resource_id", nullable = false)
  private ResourceEntity resource;

  @Column(nullable = false, length = 500)
  private String url;

  @Column(name = "public_id", nullable = false, length = 200)
  private String publicId;

  @Column(name = "display_order")
  private int displayOrder;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
