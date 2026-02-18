package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class GetMeService {

  private final UserProfileRepositoryPort users;

  public GetMeService(UserProfileRepositoryPort users) {
    this.users = users;
  }

  public UserProfile get(String sub) {
    return users
        .findByAuth0Sub(sub)
        .orElseThrow(() -> new IllegalStateException("User not provisioned (should not happen)"));
  }
}
