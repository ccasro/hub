package com.ccasro.hub.modules.iam.application.ports;

import com.ccasro.hub.modules.iam.application.AuthPrincipal;
import com.ccasro.hub.modules.iam.domain.UserProfile;

public interface EnsureLocalUserUseCase {
  UserProfile ensure(AuthPrincipal principal, String accessToken);
}
