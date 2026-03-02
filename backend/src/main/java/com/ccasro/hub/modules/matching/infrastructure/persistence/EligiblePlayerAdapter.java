package com.ccasro.hub.modules.matching.infrastructure.persistence;

import com.ccasro.hub.modules.iam.infrastructure.persistence.UserProfileJpaRepository;
import com.ccasro.hub.modules.matching.domain.MatchSkillLevel;
import com.ccasro.hub.modules.matching.domain.ports.out.EligiblePlayerPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import com.ccasro.hub.shared.domain.security.UserRole;
import com.ccasro.hub.shared.infrastructure.persistence.CityEntity;
import com.ccasro.hub.shared.infrastructure.persistence.CityJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EligiblePlayerAdapter implements EligiblePlayerPort {

  private final UserProfileJpaRepository userRepository;
  private final CityJpaRepository cityRepository;

  @Override
  public List<EligiblePlayer> findEligiblePlayers(
      GeoPoint center, double radiusKm, MatchSkillLevel skillLevel, String excludeUserId) {

    double latDelta = radiusKm / 111.0;
    double lngDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(center.latitude())));

    var citiesInBox =
        cityRepository.findByLatitudeBetweenAndLongitudeBetween(
            center.latitude() - latDelta, center.latitude() + latDelta,
            center.longitude() - lngDelta, center.longitude() + lngDelta);

    if (citiesInBox.isEmpty()) return List.of();

    var cityIds =
        citiesInBox.stream()
            .filter(
                c -> {
                  GeoPoint cityPoint = new GeoPoint(c.getLatitude(), c.getLongitude());
                  return cityPoint.isWithinRadiusKm(center, radiusKm);
                })
            .map(CityEntity::getId)
            .collect(Collectors.toSet());

    if (cityIds.isEmpty()) return List.of();

    Map<Long, GeoPoint> cityPoints =
        citiesInBox.stream()
            .filter(c -> cityIds.contains(c.getId()))
            .collect(
                Collectors.toMap(
                    CityEntity::getId, c -> new GeoPoint(c.getLatitude(), c.getLongitude())));

    return userRepository.findUsersForMatching(UserRole.PLAYER, cityIds).stream()
        .filter(u -> !u.getId().toString().equals(excludeUserId))
        .filter(
            u -> skillLevel.matches(u.getSkillLevel() != null ? u.getSkillLevel().name() : null))
        .map(
            u ->
                new EligiblePlayer(
                    u.getId().toString(),
                    u.getEmail(),
                    u.getDisplayName(),
                    u.getSkillLevel() != null ? u.getSkillLevel().name() : null,
                    cityPoints.get(u.getCityId()),
                    u.isMatchNotificationsEnabled()))
        .toList();
  }
}
