package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.application.dto.AuthPrincipal;
import com.ccasro.hub.modules.iam.application.ports.in.EnsureLocalUserUseCase;
import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfo;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfoClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EnsureLocalUserService implements EnsureLocalUserUseCase {

  private final UserProfileRepositoryPort users;
  private final Auth0UserInfoClient userInfoClient;

  public EnsureLocalUserService(
      UserProfileRepositoryPort users, Auth0UserInfoClient userInfoClient) {
    this.users = users;
    this.userInfoClient = userInfoClient;
  }

  @Override
  @Transactional
  public UserProfile ensure(AuthPrincipal principal, String accessToken) {
    return users
        .findByAuth0Sub(principal.sub())
        .orElseGet(
            () -> {
              Auth0UserInfo ui = userInfoClient.fetch(accessToken);

              if (ui == null || ui.sub() == null || !ui.sub().equals(principal.sub())) {
                throw new IllegalStateException("userinfo sub mismatch");
              }

              return users.save(UserProfile.create(principal.sub(), ui.email(), null, null, null));
            });
  }
}
