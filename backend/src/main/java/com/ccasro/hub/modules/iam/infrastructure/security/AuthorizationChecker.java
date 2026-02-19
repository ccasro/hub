package com.ccasro.hub.modules.iam.infrastructure.security;

import com.ccasro.hub.shared.application.ports.CurrentUserContextProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationChecker {

  private final CurrentUserContextProvider current;

  public boolean isAdmin() {
    return current.role().isAdmin();
  }

  public boolean isOwner() {
    return current.role().canManageVenues();
  }
}
