package com.ccasro.hub.modules.iam.domain.ports.out;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.List;
import java.util.Optional;

public interface UserProfileRepositoryPort {
  UserProfile save(UserProfile userProfile);

  Optional<UserProfile> findByAuth0Id(Auth0Id auth0Id);

  Optional<UserProfile> findById(UserId id);

  Optional<UserId> findIdByAuth0Id(Auth0Id auth0Id);

  List<UserProfile> findAll(int page, int size);

  List<UserProfile> findByOwnerRequestStatus(OwnerRequestStatus status);
}
