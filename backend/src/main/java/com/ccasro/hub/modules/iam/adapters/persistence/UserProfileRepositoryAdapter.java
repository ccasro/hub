package com.ccasro.hub.modules.iam.adapters.persistence;

import com.ccasro.hub.modules.iam.domain.UserId;
import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.UserProfileRepositoryPort;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserProfileRepositoryAdapter implements UserProfileRepositoryPort {

  private final UserProfileJpaRepository jpa;

  public UserProfileRepositoryAdapter(UserProfileJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Optional<UserProfile> findByAuth0Sub(String sub) {
    return jpa.findByAuth0Sub(sub).map(UserProfileMapper::toDomain);
  }

  @Override
  public Optional<UserId> findIdByAuth0Sub(String sub) {
    return jpa.findIdByAuth0Sub(sub).map(UserId::from);
  }

  @Override
  public UserProfile save(UserProfile user) {
    UserProfileEntity saved = jpa.save(UserProfileMapper.toEntity(user));
    return UserProfileMapper.toDomain(saved);
  }
}
