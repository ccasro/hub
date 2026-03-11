package com.ccasro.hub.modules.iam.infrastructure.persistence;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchPenaltyPort;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import com.ccasro.hub.shared.infrastructure.persistence.CityJpaRepository;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileRepositoryAdapter implements UserProfileRepositoryPort, MatchPenaltyPort {

  private final UserProfileJpaRepository jpa;
  private final UserProfileMapper mapper;
  private final CityJpaRepository cityJpa;

  @Override
  public Optional<UserProfile> findByAuth0Id(Auth0Id auth0Id) {
    return jpa.findByAuth0Id(auth0Id.value()).map(mapper::toDomain);
  }

  @Override
  public UserProfile save(UserProfile user) {
    UserProfileEntity saved =
        jpa.findByAuth0Id(user.getAuth0Id().value())
            .map(
                managed -> {
                  mapper.updateEntity(user, managed);
                  resolveCityId(user, managed);
                  return jpa.save(managed);
                })
            .orElseGet(
                () -> {
                  UserProfileEntity entity = mapper.toEntity(user);
                  resolveCityId(user, entity);
                  return jpa.save(entity);
                });

    return mapper.toDomain(saved);
  }

  private void resolveCityId(UserProfile user, UserProfileEntity entity) {
    String city = user.getCity();
    String countryCode = user.getCountryCode() != null ? user.getCountryCode().value() : null;
    if (city != null && !city.isBlank() && countryCode != null && !countryCode.isBlank()) {
      cityJpa
          .findByNameIgnoreCaseAndCountryCode(city, countryCode)
          .ifPresent(c -> entity.setCityId(c.getId()));
    }
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

  @Override
  public Map<UserId, String> findEmailsByIds(Set<UserId> ids) {
    Set<UUID> uuids = ids.stream().map(UserId::value).collect(Collectors.toSet());
    return jpa.findEmailsByIds(uuids).stream()
        .collect(Collectors.toMap(p -> new UserId(p.getId()), UserEmailProjection::getEmail));
  }

  @Override
  public void batchConfirmNoShows(
      Set<UserId> playerIds, int threshold, Instant bannedUntil, Instant now) {
    Set<UUID> uuids = playerIds.stream().map(UserId::value).collect(Collectors.toSet());
    jpa.batchConfirmNoShows(uuids, threshold, bannedUntil, now);
  }

  @Override
  public boolean tryRecordMatchCancellation(UserId userId, Instant now, Instant cooldownThreshold) {
    return jpa.tryRecordMatchCancellation(userId.value(), now, cooldownThreshold) > 0;
  }

  @Override
  public long getCooldownHoursRemaining(UserId userId) {
    return jpa.findCooldownHoursRemaining(userId.value()).orElse(0L);
  }
}
