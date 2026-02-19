package com.ccasro.hub.modules.iam.application.security;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.exception.UserProfileNotFoundException;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserContextService {

  private final CurrentUserProvider currentUser;
  private final UserProfileRepositoryPort users;

  public CurrentUserContext get() {
    Auth0Id auth0Id = new Auth0Id(currentUser.getSub());

    UserProfile profile =
        users.findByAuth0Id(auth0Id).orElseThrow(UserProfileNotFoundException::new);

    return new CurrentUserContext(profile.getId(), profile.getAuth0Id(), profile.getRole());
  }

  public boolean isAdmin() {
    return get().role() == UserRole.ADMIN;
  }

  public boolean isOwner() {
    var role = get().role();
    return role == UserRole.OWNER || role == UserRole.ADMIN;
  }
}
