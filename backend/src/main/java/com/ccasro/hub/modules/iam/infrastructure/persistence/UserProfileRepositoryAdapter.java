package com.ccasro.hub.modules.iam.infrastructure.persistence;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileRepositoryAdapter implements UserProfileRepositoryPort {

  private final UserProfileJpaRepository jpa;
  private final UserProfileMapper mapper;

  @Override
  public Optional<UserProfile> findByAuth0Id(Auth0Id auth0Id) {
    return jpa.findByAuth0Id(auth0Id.value()).map(mapper::toDomain);
  }

  @Override
  public UserProfile save(UserProfile user) {
    UserProfileEntity saved = jpa.save(mapper.toEntity(user));
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<UserProfile> findById(UserId id) {
    return jpa.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<UserId> findIdByAuth0Id(Auth0Id auth0Id) {
    return jpa.findIdByAuth0Id(auth0Id.value()).map(UserId::new);
  }

  @Override
  public List<UserProfile> findAll(int page, int size) {
    return jpa.findAll(PageRequest.of(page, size)).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<UserProfile> findByOwnerRequestStatus(OwnerRequestStatus status) {
    return jpa.findByOwnerRequestStatus(status).stream().map(mapper::toDomain).toList();
  }
}
