package com.ccasro.hub.modules.matching.domain.ports.out;

import com.ccasro.hub.modules.matching.domain.MatchSkillLevel;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import java.util.List;

public interface EligiblePlayerPort {

  record EligiblePlayer(
      String userId,
      String email,
      String displayName,
      String skillLevel,
      GeoPoint location,
      boolean matchNotificationsEnabled) {}

  List<EligiblePlayer> findEligiblePlayers(
      GeoPoint center, double radiusKm, MatchSkillLevel skillLevel, String excludeUserId);
}
