package com.ccasro.hub.modules.iam.application.usecases;

import com.ccasro.hub.modules.iam.api.dto.UpdateAvatarRequest;
import com.ccasro.hub.modules.iam.domain.ports.UserProfileRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class UpdateAvatarRequestUseCase {

  private final UserProfileRepositoryPort users;

  public UpdateAvatarRequestUseCase(UserProfileRepositoryPort users) {
    this.users = users;
  }

  public void update(String auth0Sub, UpdateAvatarRequest req) {

    var user =
        users
            .findByAuth0Sub(auth0Sub)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));

    user.updateAvatar(req.publicId(), req.url());

    users.save(user);
  }
}
