package com.ccasro.hub.modules.matching.infrastructure.persistence;

import com.ccasro.hub.modules.iam.infrastructure.persistence.UserProfileJpaRepository;
import com.ccasro.hub.modules.matching.domain.MatchSkillLevel;
import com.ccasro.hub.modules.matching.domain.ports.out.EligiblePlayerPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EligiblePlayerAdapter implements EligiblePlayerPort {

  private final UserProfileJpaRepository userRepository;

  @Override
  public List<EligiblePlayer> findEligiblePlayers(
      GeoPoint center, double radiusKm, MatchSkillLevel skillLevel, String excludeUserId) {

    return userRepository
        .findEligiblePlayers(
            center.latitude(),
            center.longitude(),
            radiusKm * 1000,
            excludeUserId,
            skillLevel.name())
        .stream()
        .map(
            p ->
                new EligiblePlayer(
                    p.getId(),
                    p.getEmail(),
                    p.getDisplayName(),
                    p.getSkillLevel(),
                    new GeoPoint(p.getCityLat(), p.getCityLng()),
                    p.isMatchNotificationsEnabled()))
        .toList();
  }

  @Override
  public int countEligiblePlayers(GeoPoint center, double radiusKm, MatchSkillLevel skillLevel) {
    return userRepository.countEligiblePlayers(
        center.latitude(), center.longitude(), radiusKm * 1000, skillLevel.name());
  }
}
