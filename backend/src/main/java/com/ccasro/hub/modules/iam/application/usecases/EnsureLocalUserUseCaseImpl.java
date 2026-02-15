package com.ccasro.hub.modules.iam.application.usecases;

import com.ccasro.hub.modules.iam.adapters.auth0.Auth0UserInfo;
import com.ccasro.hub.modules.iam.adapters.auth0.Auth0UserInfoClient;
import com.ccasro.hub.modules.iam.application.AuthPrincipal;
import com.ccasro.hub.modules.iam.application.ports.EnsureLocalUserUseCase;
import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.UserProfileRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EnsureLocalUserUseCaseImpl implements EnsureLocalUserUseCase {

  private final UserProfileRepositoryPort users;
  private final Auth0UserInfoClient userInfoClient;

  public EnsureLocalUserUseCaseImpl(
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

              return users.save(UserProfile.create(principal.sub(), ui.email(), null, null));
            });
  }
}
