package com.ccasro.hub.shared.application.ports;

import com.ccasro.hub.shared.domain.security.UserRole;

public interface CurrentUserContextProvider {
  UserRole role();
}
