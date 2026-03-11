package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.exception.UserProfileNotFoundException;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import com.ccasro.hub.shared.domain.security.UserRole;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@PreAuthorize("@authz.isAdmin()")
public class AdminUserService {

  private final UserProfileRepositoryPort repository;
  private final Clock clock;

  @Transactional(readOnly = true)
  public List<UserProfile> findAll(int page, int size) {
    return repository.findAll(page, size);
  }

  @Transactional(readOnly = true)
  public UserProfile findById(UserId id) {
    return repository.findById(id).orElseThrow(UserProfileNotFoundException::new);
  }

  @Transactional(readOnly = true)
  public List<UserProfile> findPendingOwners() {
    return repository.findByOwnerRequestStatus(OwnerRequestStatus.PENDING);
  }

  @Transactional
  public void approveOwnerRequest(UserId id) {
    UserProfile profile = repository.findById(id).orElseThrow(UserProfileNotFoundException::new);
    profile.approveOwnerRequest(clock);
    repository.save(profile);
  }

  @Transactional
  public void rejectOwnerRequest(UserId id) {
    UserProfile profile = repository.findById(id).orElseThrow(UserProfileNotFoundException::new);
    profile.rejectOwnerRequest(clock);
    repository.save(profile);
  }

  @Transactional
  public void changeRole(UserId id, UserRole newRole) {
    UserProfile profile = repository.findById(id).orElseThrow(UserProfileNotFoundException::new);
    profile.changeRole(newRole, clock);
    repository.save(profile);
  }

  @Transactional
  public void toggleActive(UserId id) {
    UserProfile profile = repository.findById(id).orElseThrow(UserProfileNotFoundException::new);
    profile.toggleActive(clock);
    repository.save(profile);
  }
}
