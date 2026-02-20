package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.exception.UserProfileNotFoundException;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMeService {

  private final UserProfileRepositoryPort users;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public UserProfile execute() {
    Auth0Id auth0Id = new Auth0Id(currentUser.getSub());
    return users.findByAuth0Id(auth0Id).orElseThrow(UserProfileNotFoundException::new);
  }
}
