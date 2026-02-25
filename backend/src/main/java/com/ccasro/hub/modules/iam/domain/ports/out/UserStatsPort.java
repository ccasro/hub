package com.ccasro.hub.modules.iam.domain.ports.out;

import com.ccasro.hub.shared.domain.security.UserRole;

public interface UserStatsPort {
  long countTotal();

  long countByRole(UserRole role);

  long countPendingOwnerRequests();
}
