package com.ccasro.hub.modules.iam.domain;

import java.time.Instant;

public class UserProfile {

  private final UserId id;
  private final String auth0Sub;
  private String email;
  private String displayName;
  private String avatarUrl;
  private final Instant createdAt;
  private Instant updatedAt;

  private UserProfile(
      UserId id,
      String auth0Sub,
      String email,
      String displayName,
      String avatarUrl,
      Instant createdAt,
      Instant updatedAt) {
    if (auth0Sub == null || auth0Sub.isBlank()) {
      throw new IllegalArgumentException("auth0Sub is required");
    }
    if (id == null) {
      throw new IllegalArgumentException("id is required");
    }

    this.id = id;
    this.auth0Sub = auth0Sub;
    this.email = email;
    this.displayName = displayName;
    this.avatarUrl = avatarUrl;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static UserProfile create(
      String auth0Sub, String email, String displayName, String avatarUrl) {
    Instant now = Instant.now();
    return new UserProfile(UserId.newId(), auth0Sub, email, displayName, avatarUrl, now, now);
  }

  public void updateBasicProfile(String email, String displayName, String avatarUrl) {
    if (email != null) this.email = email;
    if (displayName != null) this.displayName = displayName;
    if (avatarUrl != null) this.avatarUrl = avatarUrl;
    this.updatedAt = Instant.now();
  }

  public static UserProfile rehydrate(
      UserId id,
      String auth0Sub,
      String email,
      String displayName,
      String avatarUrl,
      Instant createdAt,
      Instant updatedAt) {
    return new UserProfile(id, auth0Sub, email, displayName, avatarUrl, createdAt, updatedAt);
  }

  public UserId getId() {
    return id;
  }

  public String getAuth0Sub() {
    return auth0Sub;
  }

  public String getEmail() {
    return email;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
