package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.UpdateAvatarRequest;
import org.springframework.stereotype.Service;

@Service
public class UpdateAvatarService {

  private final UserProfileRepositoryPort users;

  public UpdateAvatarService(UserProfileRepositoryPort users) {
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
