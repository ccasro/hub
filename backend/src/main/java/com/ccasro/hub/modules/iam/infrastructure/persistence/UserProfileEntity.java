package com.ccasro.hub.modules.iam.infrastructure.persistence;

import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import com.ccasro.hub.modules.iam.domain.valueobjects.SkillLevel;
import com.ccasro.hub.modules.iam.domain.valueobjects.SportPreference;
import com.ccasro.hub.shared.domain.security.UserRole;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "user_profile")
@Data
public class UserProfileEntity {

  protected UserProfileEntity() {}

  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "auth0_id", unique = true, nullable = false, length = 128)
  private String auth0Id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(name = "email_verified")
  private boolean emailVerified;

  @Column(name = "display_name", length = 100)
  private String displayName;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @Column(name = "avatar_url", length = 500)
  private String avatarUrl;

  @Column(name = "avatar_public_id", length = 200)
  private String avatarPublicId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "owner_request_status", length = 20)
  private OwnerRequestStatus ownerRequestStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "preferred_sport", length = 20)
  private SportPreference preferredSport;

  @Enumerated(EnumType.STRING)
  @Column(name = "skill_level", length = 20)
  private SkillLevel skillLevel;

  @Column(length = 100)
  private String city;

  @Column(name = "country_code", length = 3)
  private String countryCode;

  @Column(nullable = false)
  private boolean active;

  @Column(name = "onboarding_completed")
  private boolean onboardingCompleted;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "last_login_at")
  private Instant lastLoginAt;
}
