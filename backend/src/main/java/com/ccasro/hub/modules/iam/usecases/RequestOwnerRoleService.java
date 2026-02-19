package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.exception.UserProfileNotFoundException;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestOwnerRoleService {

  private final UserProfileRepositoryPort repository;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  public void execute() {
    UserProfile profile =
        repository.findById(currentUser.getUserId()).orElseThrow(UserProfileNotFoundException::new);
    profile.requestOwnerRole(clock);
    repository.save(profile);
  }
}
