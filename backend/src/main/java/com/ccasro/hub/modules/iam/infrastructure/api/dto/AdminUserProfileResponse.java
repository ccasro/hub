package com.ccasro.hub.modules.iam.infrastructure.api.dto;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import java.time.Instant;
import java.util.UUID;

public record AdminUserProfileResponse(
    UUID id,
    String email,
    boolean emailVerified,
    String displayName,
    String description,
    String phoneNumber,
    String avatarUrl,
    String role,
    String ownerRequestStatus,
    String preferredSport,
    String skillLevel,
    String city,
    String countryCode,
    boolean active,
    boolean onboardingCompleted,
    Instant lastLoginAt,
    Instant createdAt,
    Instant updatedAt) {
  public static AdminUserProfileResponse from(UserProfile p) {
    return new AdminUserProfileResponse(
        p.getId().value(),
        p.getEmail().value(),
        p.isEmailVerified(),
        p.getDisplayName() != null ? p.getDisplayName().value() : null,
        p.getDescription(),
        p.getPhoneNumber() != null ? p.getPhoneNumber().value() : null,
        p.getAvatar() != null ? p.getAvatar().url() : null,
        p.getRole().name(),
        p.getOwnerRequestStatus() != OwnerRequestStatus.NONE
            ? p.getOwnerRequestStatus().name()
            : null,
        p.getPreferredSport() != null ? p.getPreferredSport().name() : null,
        p.getSkillLevel() != null ? p.getSkillLevel().name() : null,
        p.getCity(),
        p.getCountryCode() != null ? p.getCountryCode().value() : null,
        p.isActive(),
        p.isOnboardingCompleted(),
        p.getLastLoginAt(),
        p.getCreatedAt(),
        p.getUpdatedAt());
  }
}
