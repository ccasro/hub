package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.exception.UserProfileNotFoundException;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.modules.iam.domain.valueobjects.DisplayName;
import com.ccasro.hub.modules.iam.domain.valueobjects.PhoneNumber;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.CountryCode;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateMeService {

  private final UserProfileRepositoryPort repository;
  private final CurrentUserProvider currentUser;
  private final Clock clock;

  @Transactional
  public UserProfile execute(UpdateMeCommand command) {
    Auth0Id auth0Id = new Auth0Id(currentUser.getSub());
    UserProfile profile =
        repository.findByAuth0Id(auth0Id).orElseThrow(UserProfileNotFoundException::new);

    profile.updateProfile(
        new DisplayName(command.displayName()),
        command.description(),
        command.phoneNumber() != null ? new PhoneNumber(command.phoneNumber()) : null,
        command.city(),
        command.countryCode() != null ? new CountryCode(command.countryCode()) : null,
        command.preferredSport(),
        command.skillLevel(),
        command.matchNotificationsEnabled(),
        clock);

    repository.save(profile);
    return profile;
  }
}
