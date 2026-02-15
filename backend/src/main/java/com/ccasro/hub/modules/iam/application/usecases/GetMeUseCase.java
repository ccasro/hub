package com.ccasro.hub.modules.iam.application.usecases;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.UserProfileRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class GetMeUseCase {

  private final UserProfileRepositoryPort users;

  public GetMeUseCase(UserProfileRepositoryPort users) {
    this.users = users;
  }

  public UserProfile get(String sub) {
    return users
        .findByAuth0Sub(sub)
        .orElseThrow(() -> new IllegalStateException("User not provisioned (should not happen)"));
  }
}
