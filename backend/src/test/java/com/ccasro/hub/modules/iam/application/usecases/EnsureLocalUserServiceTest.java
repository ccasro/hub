package com.ccasro.hub.modules.iam.application.usecases;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.ccasro.hub.modules.iam.application.dto.AuthPrincipal;
import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfo;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfoClient;
import com.ccasro.hub.modules.iam.usecases.EnsureLocalUserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EnsureLocalUserServiceTest {

  @Test
  void ensure_returns_existing_user_and_does_not_call_auth0() {
    var users = mock(UserProfileRepositoryPort.class);
    var auth0 = mock(Auth0UserInfoClient.class);
    var uc = new EnsureLocalUserService(users, auth0);

    var principal = new AuthPrincipal("auth0|123");

    var existing = UserProfile.create("auth0|123", "existing@test.local", null, null, null);

    when(users.findByAuth0Sub("auth0|123")).thenReturn(Optional.of(existing));

    var result = uc.ensure(principal, "token");

    assertThat(result).isSameAs(existing);
    verify(auth0, never()).fetch(any());
    verify(users, never()).save(any());
  }

  @Test
  void ensure_creates_user_when_missing_and_userinfo_matches_sub() {

    var users = mock(UserProfileRepositoryPort.class);
    var auth0 = mock(Auth0UserInfoClient.class);
    var uc = new EnsureLocalUserService(users, auth0);

    var principal = new AuthPrincipal("auth0|123");

    when(users.findByAuth0Sub("auth0|123")).thenReturn(Optional.empty());

    when(auth0.fetch("good-token")).thenReturn(new Auth0UserInfo("auth0|123", "user@test.local"));

    when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var result = uc.ensure(principal, "good-token");

    assertThat(result.getAuth0Sub()).isEqualTo("auth0|123");
    assertThat(result.getEmail()).isEqualTo("user@test.local");

    verify(auth0).fetch("good-token");
    verify(users).save(any());
  }

  @Test
  void ensure_throws_when_userinfo_sub_mismatch() {
    var users = mock(UserProfileRepositoryPort.class);
    var auth0 = mock(Auth0UserInfoClient.class);
    var uc = new EnsureLocalUserService(users, auth0);

    var principal = new AuthPrincipal("auth0|123");

    when(users.findByAuth0Sub("auth0|123")).thenReturn(Optional.empty());

    when(auth0.fetch("bad-token")).thenReturn(new Auth0UserInfo("auth0|OTHER", "user@test.local"));

    assertThatThrownBy(() -> uc.ensure(principal, "bad-token"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("userinfo sub mismatch");

    verify(users, never()).save(any());
  }
}
