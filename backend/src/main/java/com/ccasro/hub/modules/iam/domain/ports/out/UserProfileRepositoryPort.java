package com.ccasro.hub.modules.iam.domain.ports.out;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.valueobjects.UserId;
import java.util.Optional;

public interface UserProfileRepositoryPort {
  Optional<UserProfile> findByAuth0Sub(String sub);

  Optional<UserId> findIdByAuth0Sub(String sub);

  UserProfile save(UserProfile user);
}
