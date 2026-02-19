package com.ccasro.hub.modules.iam.infrastructure.persistence;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.valueobjects.*;
import com.ccasro.hub.shared.domain.valueobjects.CountryCode;
import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

  public UserProfile toDomain(UserProfileEntity e) {
    return UserProfile.reconstitute(
        new UserId(e.getId()),
        new Auth0Id(e.getAuth0Id()),
        e.getEmail() != null ? new Email(e.getEmail()) : null,
        e.isEmailVerified(),
        e.getDisplayName() != null ? new DisplayName(e.getDisplayName()) : null,
        e.getDescription(),
        e.getPhoneNumber() != null ? new PhoneNumber(e.getPhoneNumber()) : null,
        e.getAvatarUrl() != null ? new ImageUrl(e.getAvatarUrl(), e.getAvatarPublicId()) : null,
        e.getRole(),
        e.getOwnerRequestStatus() != null ? e.getOwnerRequestStatus() : OwnerRequestStatus.NONE,
        e.getPreferredSport(),
        e.getSkillLevel(),
        e.getCity(),
        e.getCountryCode() != null ? new CountryCode(e.getCountryCode()) : null,
        e.isActive(),
        e.isOnboardingCompleted(),
        e.getCreatedAt(),
        e.getUpdatedAt(),
        e.getLastLoginAt());
  }

  public UserProfileEntity toEntity(UserProfile d) {
    UserProfileEntity e = new UserProfileEntity();
    e.setId(d.getId().value());
    e.setAuth0Id(d.getAuth0Id().value());
    e.setEmail(d.getEmail() != null ? d.getEmail().value() : null);
    e.setEmailVerified(d.isEmailVerified());
    e.setDisplayName(d.getDisplayName() != null ? d.getDisplayName().value() : null);
    e.setDescription(d.getDescription());
    e.setPhoneNumber(d.getPhoneNumber() != null ? d.getPhoneNumber().value() : null);
    e.setAvatarUrl(d.getAvatar() != null ? d.getAvatar().url() : null);
    e.setAvatarPublicId(d.getAvatar() != null ? d.getAvatar().publicId() : null);
    e.setRole(d.getRole());
    e.setOwnerRequestStatus(
        d.getOwnerRequestStatus() == OwnerRequestStatus.NONE ? null : d.getOwnerRequestStatus());
    e.setPreferredSport(d.getPreferredSport());
    e.setSkillLevel(d.getSkillLevel());
    e.setCity(d.getCity());
    e.setCountryCode(d.getCountryCode() != null ? d.getCountryCode().value() : null);
    e.setActive(d.isActive());
    e.setOnboardingCompleted(d.isOnboardingCompleted());
    e.setCreatedAt(d.getCreatedAt());
    e.setUpdatedAt(d.getUpdatedAt());
    e.setLastLoginAt(d.getLastLoginAt());
    return e;
  }
}
