package com.ccasro.hub.modules.iam.infrastructure.persistence;

import com.ccasro.hub.modules.iam.domain.ports.out.UserStatsPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import com.ccasro.hub.shared.domain.security.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStatsAdapter implements UserStatsPort {

  private final UserProfileJpaRepository userRepository;

  @Override
  public long countTotal() {
    return userRepository.count();
  }

  @Override
  public long countByRole(UserRole role) {
    return userRepository.countByRole(role);
  }

  @Override
  public long countPendingOwnerRequests() {
    return userRepository.countByOwnerRequestStatus(OwnerRequestStatus.PENDING);
  }
}
