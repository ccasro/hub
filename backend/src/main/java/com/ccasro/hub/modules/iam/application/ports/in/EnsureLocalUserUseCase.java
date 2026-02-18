package com.ccasro.hub.modules.iam.application.ports.in;

import com.ccasro.hub.modules.iam.application.dto.AuthPrincipal;
import com.ccasro.hub.modules.iam.domain.UserProfile;

public interface EnsureLocalUserUseCase {
  UserProfile ensure(AuthPrincipal principal, String accessToken);
}
