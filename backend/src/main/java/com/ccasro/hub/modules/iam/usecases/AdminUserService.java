package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.exception.UserProfileNotFoundException;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.AdminUserProfileResponse;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.UserProfileResponse;
import com.ccasro.hub.shared.application.ports.CurrentUserContextProvider;
import com.ccasro.hub.shared.domain.security.UserRole;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

  private final UserProfileRepositoryPort repository;
  private final CurrentUserContextProvider current;
  private final Clock clock;

  private void requireAdmin() {
    if (!current.role().isAdmin()) {
      throw new AccessDeniedException("Admin access required");
    }
  }

  public List<AdminUserProfileResponse> findAll(int page, int size) {
    requireAdmin();
    return repository.findAll(page, size).stream().map(AdminUserProfileResponse::from).toList();
  }

  public UserProfileResponse findById(UserId id) {
    requireAdmin();
    return UserProfileResponse.from(
        repository.findById(id).orElseThrow(UserProfileNotFoundException::new));
  }

  public List<UserProfileResponse> findPendingOwners() {
    requireAdmin();
    return repository.findByOwnerRequestStatus(OwnerRequestStatus.PENDING).stream()
        .map(UserProfileResponse::from)
        .toList();
  }

  public void approveOwnerRequest(UserId id) {
    requireAdmin();
    UserProfile profile = repository.findById(id).orElseThrow(UserProfileNotFoundException::new);
    profile.approveOwnerRequest(clock);
    repository.save(profile);
  }

  public void rejectOwnerRequest(UserId id) {
    requireAdmin();
    UserProfile profile = repository.findById(id).orElseThrow(UserProfileNotFoundException::new);
    profile.rejectOwnerRequest(clock);
    repository.save(profile);
  }

  public void changeRole(UserId id, UserRole newRole) {
    requireAdmin();
    UserProfile profile = repository.findById(id).orElseThrow(UserProfileNotFoundException::new);
    profile.changeRole(newRole, clock);
    repository.save(profile);
  }

  public void toggleActive(UserId id) {
    requireAdmin();
    UserProfile profile = repository.findById(id).orElseThrow(UserProfileNotFoundException::new);
    profile.toggleActive(clock);
    repository.save(profile);
  }
}
