package com.ccasro.hub.modules.iam.infrastructure.persistence;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.valueobjects.UserId;

final class UserProfileMapper {

  static UserProfile toDomain(UserProfileEntity e) {
    return UserProfile.rehydrate(
        UserId.from(e.id),
        e.auth0Sub,
        e.email,
        e.displayName,
        e.avatarPublicId,
        e.avatarUrl,
        e.createdAt,
        e.updatedAt);
  }

  static UserProfileEntity toEntity(UserProfile d) {
    var e = new UserProfileEntity();
    e.id = d.getId().value();
    e.auth0Sub = d.getAuth0Sub();
    e.email = d.getEmail();
    e.displayName = d.getDisplayName();
    e.avatarPublicId = d.getAvatarPublicId();
    e.avatarUrl = d.getAvatarUrl();
    e.createdAt = d.getCreatedAt();
    e.updatedAt = d.getUpdatedAt();
    return e;
  }

  private UserProfileMapper() {}
}
