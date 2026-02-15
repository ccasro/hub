package com.ccasro.hub.modules.iam.adapters.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
class UserProfileEntity {

  @Id UUID id;

  @Column(name = "auth0_sub", nullable = false, unique = true)
  String auth0Sub;

  String email;

  @Column(name = "display_name")
  String displayName;

  @Column(name = "avatar_url")
  String avatarUrl;

  @Column(name = "avatar_public_id")
  String avatarPublicId;

  @Column(name = "created_at", nullable = false)
  Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  Instant updatedAt;

  protected UserProfileEntity() {}
}
