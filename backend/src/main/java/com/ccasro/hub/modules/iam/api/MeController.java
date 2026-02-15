package com.ccasro.hub.modules.iam.api;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.UserProfileRepositoryPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

  private final UserProfileRepositoryPort users;
  private final CurrentUserProvider currentUser;

  public MeController(UserProfileRepositoryPort users, CurrentUserProvider currentUser) {
    this.users = users;
    this.currentUser = currentUser;
  }

  @GetMapping("/me")
  public MeResponse me() {
    var sub = currentUser.getSub();

    UserProfile u =
        users
            .findByAuth0Sub(sub)
            .orElseThrow(
                () -> new IllegalStateException("User not provisioned (should not happen)"));

    return new MeResponse(u.getId().toString(), u.getEmail(), u.getDisplayName(), u.getAvatarUrl());
  }
}
