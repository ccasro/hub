package com.ccasro.hub.modules.iam.domain;

import com.ccasro.hub.modules.iam.domain.valueobjects.*;
import com.ccasro.hub.shared.domain.security.UserRole;
import com.ccasro.hub.shared.domain.valueobjects.CountryCode;
import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class UserProfile {

  private final UserId id;
  private final Auth0Id auth0Id;
  private Email email;
  private boolean emailVerified;

  private DisplayName displayName;
  private String description;
  private PhoneNumber phoneNumber;

  private ImageUrl avatar;

  private UserRole role;
  private OwnerRequestStatus ownerRequestStatus;
  private SportPreference preferredSport;
  private SkillLevel skillLevel;

  private String city;
  private CountryCode countryCode;

  private boolean active;
  private boolean onboardingCompleted;

  private int noShowCount;
  private Instant matchBannedUntil;
  private Instant lastMatchCancelledAt;
  private boolean matchNotificationsEnabled;

  private static final int NO_SHOW_BAN_THRESHOLD = 3;
  private static final Duration BAN_DURATION = Duration.ofDays(30);

  private final Instant createdAt;
  private Instant updatedAt;
  private Instant lastLoginAt;

  private UserProfile(UserId id, Auth0Id auth0Id, Email email, boolean emailVerified, Instant now) {
    this.id = id;
    this.auth0Id = auth0Id;
    this.email = email;
    this.emailVerified = emailVerified;
    this.role = UserRole.PLAYER;
    this.ownerRequestStatus = OwnerRequestStatus.NONE;
    this.active = true;
    this.onboardingCompleted = false;
    this.createdAt = now;
    this.updatedAt = now;
    this.lastLoginAt = now;
  }

  private UserProfile(UserId id, Auth0Id auth0Id, Instant now) {
    this(id, auth0Id, null, false, now);
  }

  public static UserProfile create(Auth0Id auth0Id, Clock clock) {
    Instant now = clock.instant();
    return new UserProfile(UserId.newId(), auth0Id, now);
  }

  public void updateEmailFromRaw(String raw, Clock clock) {
    if (raw == null) return;
    String cleaned = raw.trim();
    if (cleaned.isEmpty()) return;

    this.email = new Email(cleaned);
    this.emailVerified = false;
    this.updatedAt = clock.instant();
  }

  public static UserProfile reconstitute(
      UserId id,
      Auth0Id auth0Id,
      Email email,
      boolean emailVerified,
      DisplayName displayName,
      String description,
      PhoneNumber phoneNumber,
      ImageUrl avatar,
      UserRole role,
      OwnerRequestStatus ownerRequestStatus,
      SportPreference preferredSport,
      SkillLevel skillLevel,
      String city,
      CountryCode countryCode,
      boolean active,
      boolean onboardingCompleted,
      Instant createdAt,
      Instant updatedAt,
      Instant lastLoginAt,
      int noShowCount,
      Instant matchBannedUntil,
      Instant lastMatchCancelledAt,
      boolean matchNotificationsEnabled) {
    UserProfile profile = new UserProfile(id, auth0Id, email, emailVerified, createdAt);
    profile.displayName = displayName;
    profile.description = description;
    profile.phoneNumber = phoneNumber;
    profile.avatar = avatar;
    profile.role = role;
    profile.ownerRequestStatus = ownerRequestStatus;
    profile.preferredSport = preferredSport;
    profile.skillLevel = skillLevel;
    profile.city = city;
    profile.countryCode = countryCode;
    profile.active = active;
    profile.onboardingCompleted = onboardingCompleted;
    profile.updatedAt = updatedAt;
    profile.lastLoginAt = lastLoginAt;
    profile.noShowCount = noShowCount;
    profile.matchBannedUntil = matchBannedUntil;
    profile.lastMatchCancelledAt = lastMatchCancelledAt;
    profile.matchNotificationsEnabled = matchNotificationsEnabled;
    return profile;
  }

  public void confirmNoShow(Clock clock) {
    this.noShowCount++;
    if (this.noShowCount >= NO_SHOW_BAN_THRESHOLD) {
      this.matchBannedUntil = clock.instant().plus(BAN_DURATION);
    }
    this.updatedAt = clock.instant();
  }

  public boolean isMatchBanned(Clock clock) {
    return matchBannedUntil != null && clock.instant().isBefore(matchBannedUntil);
  }

  public void recordLogin(Clock clock) {
    this.lastLoginAt = clock.instant();
    this.updatedAt = clock.instant();
  }

  public void updateProfile(
      DisplayName displayName,
      String description,
      PhoneNumber phoneNumber,
      String city,
      CountryCode countryCode,
      SportPreference preferredSport,
      SkillLevel skillLevel,
      Boolean matchNotificationsEnabled,
      Clock clock) {
    this.displayName = displayName;
    this.description = description;
    this.phoneNumber = phoneNumber;
    this.city = city;
    this.countryCode = countryCode;
    this.preferredSport = preferredSport;
    this.skillLevel = skillLevel;
    if (matchNotificationsEnabled != null) {
      this.matchNotificationsEnabled = matchNotificationsEnabled;
    }
    this.updatedAt = clock.instant();
    checkOnboardingCompleted();
  }

  public void updateAvatar(ImageUrl newAvatar, Clock clock) {
    this.avatar = newAvatar;
    this.updatedAt = clock.instant();
  }

  public void requestOwnerRole(Clock clock) {
    if (this.role == UserRole.OWNER || this.role == UserRole.ADMIN)
      throw new IllegalStateException("You already have owner or admin role");
    if (this.ownerRequestStatus == OwnerRequestStatus.PENDING)
      throw new IllegalStateException("You already have a pending request");
    this.ownerRequestStatus = OwnerRequestStatus.PENDING;
    this.updatedAt = clock.instant();
  }

  public void approveOwnerRequest(Clock clock) {
    if (this.ownerRequestStatus != OwnerRequestStatus.PENDING)
      throw new IllegalStateException("There is no pending request");
    this.role = UserRole.OWNER;
    this.ownerRequestStatus = OwnerRequestStatus.APPROVED;
    this.updatedAt = clock.instant();
  }

  public void rejectOwnerRequest(Clock clock) {
    if (this.ownerRequestStatus != OwnerRequestStatus.PENDING)
      throw new IllegalStateException("There is no pending request");
    this.ownerRequestStatus = OwnerRequestStatus.REJECTED;
    this.updatedAt = clock.instant();
  }

  public void changeRole(UserRole newRole, Clock clock) {
    this.role = newRole;
    this.updatedAt = clock.instant();
  }

  public void toggleActive(Clock clock) {
    this.active = !this.active;
    this.updatedAt = clock.instant();
  }

  public boolean isOwner() {
    return this.role == UserRole.OWNER || this.role == UserRole.ADMIN;
  }

  private void checkOnboardingCompleted() {
    this.onboardingCompleted =
        this.displayName != null && this.city != null && !this.city.isBlank();
  }

  public UserId getId() {
    return id;
  }

  public Auth0Id getAuth0Id() {
    return auth0Id;
  }

  public Email getEmail() {
    return email;
  }

  public Optional<Email> email() {
    return Optional.ofNullable(email);
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  public DisplayName getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public PhoneNumber getPhoneNumber() {
    return phoneNumber;
  }

  public ImageUrl getAvatar() {
    return avatar;
  }

  public UserRole getRole() {
    return role;
  }

  public OwnerRequestStatus getOwnerRequestStatus() {
    return ownerRequestStatus;
  }

  public SportPreference getPreferredSport() {
    return preferredSport;
  }

  public SkillLevel getSkillLevel() {
    return skillLevel;
  }

  public String getCity() {
    return city;
  }

  public CountryCode getCountryCode() {
    return countryCode;
  }

  public boolean isActive() {
    return active;
  }

  public boolean isOnboardingCompleted() {
    return onboardingCompleted;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Instant getLastLoginAt() {
    return lastLoginAt;
  }

  public int getNoShowCount() {
    return noShowCount;
  }

  public Instant getMatchBannedUntil() {
    return matchBannedUntil;
  }

  public Instant getLastMatchCancelledAt() {
    return lastMatchCancelledAt;
  }

  public boolean isMatchNotificationsEnabled() {
    return matchNotificationsEnabled;
  }
}
