package com.ccasro.hub.modules.iam.domain;

import com.ccasro.hub.modules.iam.domain.valueobjects.UserId;
import com.ccasro.hub.shared.domain.MediaKey;
import java.time.Instant;

public class UserProfile {

  private final UserId id;
  private final String auth0Sub;
  private String email;
  private String displayName;
  private String avatarPublicId;
  private String avatarUrl;
  private final Instant createdAt;
  private Instant updatedAt;

  private UserProfile(
      UserId id,
      String auth0Sub,
      String email,
      String displayName,
      String avatarPublicId,
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
    this.avatarPublicId = avatarPublicId;
    this.avatarUrl = avatarUrl;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static UserProfile create(
      String auth0Sub, String email, String displayName, String avatarPublicId, String avatarUrl) {
    Instant now = Instant.now();
    return new UserProfile(
        UserId.newId(), auth0Sub, email, displayName, avatarPublicId, avatarUrl, now, now);
  }

  public void updateAvatar(String avatarPublicId, String avatarUrl) {
    if (avatarPublicId == null || avatarPublicId.isBlank()) {
      throw new IllegalArgumentException("avatarPublicId is required");
    }

    String expectedPrefix = MediaKey.avatarPrefix(this.auth0Sub);
    if (!avatarPublicId.startsWith(expectedPrefix)) {
      throw new IllegalArgumentException("avatar does not belong to user");
    }
    this.avatarPublicId = avatarPublicId;
    if (avatarUrl != null && !avatarUrl.isBlank()) {
      this.avatarUrl = avatarUrl;
    }
    this.updatedAt = Instant.now();
  }

  public static UserProfile rehydrate(
      UserId id,
      String auth0Sub,
      String email,
      String displayName,
      String avatarPublicId,
      String avatarUrl,
      Instant createdAt,
      Instant updatedAt) {
    return new UserProfile(
        id, auth0Sub, email, displayName, avatarPublicId, avatarUrl, createdAt, updatedAt);
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

  public String getAvatarPublicId() {
    return avatarPublicId;
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
