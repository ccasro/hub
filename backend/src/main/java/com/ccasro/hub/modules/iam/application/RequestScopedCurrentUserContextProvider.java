package com.ccasro.hub.modules.iam.application;

import com.ccasro.hub.modules.iam.domain.exception.UserProfileNotFoundException;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.shared.application.ports.CurrentUserContextProvider;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class RequestScopedCurrentUserContextProvider implements CurrentUserContextProvider {

  private final CurrentUserProvider currentUser;
  private final UserProfileRepositoryPort users;

  private UserRole cachedRole;

  @Override
  public UserRole role() {
    if (cachedRole != null) return cachedRole;

    var profile =
        users.findById(currentUser.getUserId()).orElseThrow(UserProfileNotFoundException::new);

    cachedRole = profile.getRole();
    return cachedRole;
  }
}
