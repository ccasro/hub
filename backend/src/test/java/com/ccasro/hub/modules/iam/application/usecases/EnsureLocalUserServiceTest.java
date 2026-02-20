package com.ccasro.hub.modules.iam.application.usecases;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.ccasro.hub.modules.iam.application.dto.AuthPrincipal;
import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfo;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfoClient;
import com.ccasro.hub.modules.iam.usecases.EnsureLocalUserService;
import com.ccasro.hub.shared.domain.security.UserRole;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

class EnsureLocalUserServiceTest {

  private static final String ADMIN_EMAIL = "admin@test.com";
  private static final Instant NOW = Instant.parse("2026-02-18T10:00:00Z");
  private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

  private UserProfileRepositoryPort users;
  private Auth0UserInfoClient auth0Client;
  private EnsureLocalUserService uc;

  @BeforeEach
  void setUp() {
    users = mock(UserProfileRepositoryPort.class);
    auth0Client = mock(Auth0UserInfoClient.class);
    uc = new EnsureLocalUserService(users, auth0Client, CLOCK);
    ReflectionTestUtils.setField(uc, "adminEmail", ADMIN_EMAIL);
  }

  @Test
  void ensure_returns_existing_user_updates_lastLogin_and_never_calls_auth0() {
    var principal = new AuthPrincipal("auth0|123");
    var existing = UserProfile.create(new Auth0Id("auth0|123"), CLOCK);

    when(users.findByAuth0Id(new Auth0Id("auth0|123"))).thenReturn(Optional.of(existing));
    when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = uc.ensure(principal, "any-token");

    assertThat(result).isSameAs(existing);
    assertThat(result.getLastLoginAt()).isEqualTo(NOW);
    verify(auth0Client, never()).getUserInfo(any());
    verify(users).save(existing);
  }

  @Test
  void ensure_creates_new_player_when_email_does_not_match_admin() {
    var principal = new AuthPrincipal("auth0|123");

    when(users.findByAuth0Id(new Auth0Id("auth0|123"))).thenReturn(Optional.empty());
    when(auth0Client.getUserInfo("token"))
        .thenReturn(new Auth0UserInfo("auth0|123", "user@test.com"));
    when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = uc.ensure(principal, "token");

    assertThat(result.getAuth0Id()).isEqualTo(new Auth0Id("auth0|123"));
    assertThat(result.getRole()).isEqualTo(UserRole.PLAYER);
    assertThat(result.getOwnerRequestStatus()).isEqualTo(OwnerRequestStatus.NONE);
    verify(auth0Client).getUserInfo("token");
    verify(users).save(any(UserProfile.class));
  }

  @Test
  void ensure_creates_new_user_with_admin_role_when_email_matches_admin_config() {
    var principal = new AuthPrincipal("auth0|admin");

    when(users.findByAuth0Id(new Auth0Id("auth0|admin"))).thenReturn(Optional.empty());
    when(auth0Client.getUserInfo("token"))
        .thenReturn(new Auth0UserInfo("auth0|admin", ADMIN_EMAIL));
    when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = uc.ensure(principal, "token");

    assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);
    verify(users).save(any(UserProfile.class));
  }

  @Test
  void ensure_admin_promotion_is_case_insensitive() {
    var principal = new AuthPrincipal("auth0|admin");

    when(users.findByAuth0Id(new Auth0Id("auth0|admin"))).thenReturn(Optional.empty());
    when(auth0Client.getUserInfo("token"))
        .thenReturn(new Auth0UserInfo("auth0|admin", "ADMIN@TEST.COM"));
    when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = uc.ensure(principal, "token");

    assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);
  }

  @Test
  void ensure_does_not_promote_when_admin_email_not_configured() {
    ReflectionTestUtils.setField(uc, "adminEmail", "");

    var principal = new AuthPrincipal("auth0|123");

    when(users.findByAuth0Id(new Auth0Id("auth0|123"))).thenReturn(Optional.empty());
    when(auth0Client.getUserInfo("token"))
        .thenReturn(new Auth0UserInfo("auth0|123", "user@test.com"));
    when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = uc.ensure(principal, "token");

    assertThat(result.getRole()).isEqualTo(UserRole.PLAYER); // sigue PLAYER
  }

  @Test
  void ensure_throws_when_userinfo_sub_does_not_match_principal() {
    var principal = new AuthPrincipal("auth0|123");

    when(users.findByAuth0Id(new Auth0Id("auth0|123"))).thenReturn(Optional.empty());
    when(auth0Client.getUserInfo("bad-token"))
        .thenReturn(new Auth0UserInfo("auth0|OTHER", "user@test.com"));

    assertThatThrownBy(() -> uc.ensure(principal, "bad-token"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("userinfo sub mismatch");

    verify(users, never()).save(any());
  }

  @Test
  void ensure_throws_when_userinfo_is_null() {
    var principal = new AuthPrincipal("auth0|123");

    when(users.findByAuth0Id(new Auth0Id("auth0|123"))).thenReturn(Optional.empty());
    when(auth0Client.getUserInfo("token")).thenReturn(null);

    assertThatThrownBy(() -> uc.ensure(principal, "token"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("userinfo sub mismatch");

    verify(users, never()).save(any());
  }

  @Test
  void ensure_returns_existing_user_on_race_condition_duplicate_save() {
    var principal = new AuthPrincipal("auth0|123");
    var existing = UserProfile.create(new Auth0Id("auth0|123"), CLOCK);

    when(users.findByAuth0Id(new Auth0Id("auth0|123")))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(existing));

    when(auth0Client.getUserInfo("token"))
        .thenReturn(new Auth0UserInfo("auth0|123", "user@test.com"));

      when(users.save(any()))
              .thenThrow(new DataIntegrityViolationException("duplicate key")) // save(created)
              .thenReturn(existing);

      var result = uc.ensure(principal, "token");

    assertThat(result).isSameAs(existing);
  }
}
