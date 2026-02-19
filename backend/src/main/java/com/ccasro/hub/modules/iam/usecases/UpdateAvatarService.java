package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.exception.UserProfileNotFoundException;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAvatarService {

  private final UserProfileRepositoryPort users;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  public UserProfile execute(ImageUrl newAvatar) {
    Auth0Id auth0Id = new Auth0Id(currentUser.getSub());

    UserProfile profile =
        users.findByAuth0Id(auth0Id).orElseThrow(UserProfileNotFoundException::new);

    profile.updateAvatar(newAvatar, clock);

    users.save(profile);
    return profile;
  }
}
