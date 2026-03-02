package com.ccasro.hub.modules.matching.domain;

import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.Instant;

public class MatchPlayer {

  private final UserId playerId;
  private final PlayerTeam team;
  private final PlayerRole role;
  private final Instant joinedAt;

  private MatchPlayer(UserId playerId, PlayerTeam team, PlayerRole role, Instant joinedAt) {
    this.playerId = playerId;
    this.team = team;
    this.role = role;
    this.joinedAt = joinedAt;
  }

  public static MatchPlayer organizer(UserId playerId, Clock clock) {
    return new MatchPlayer(playerId, PlayerTeam.TEAM_1, PlayerRole.ORGANIZER, clock.instant());
  }

  public static MatchPlayer guest(UserId playerId, PlayerTeam team, Clock clock) {
    return new MatchPlayer(playerId, team, PlayerRole.GUEST, clock.instant());
  }

  public static MatchPlayer reconstitute(
      UserId playerId, PlayerTeam team, PlayerRole role, Instant joinedAt) {
    return new MatchPlayer(playerId, team, role, joinedAt);
  }

  public UserId getPlayerId() {
    return playerId;
  }

  public PlayerTeam getTeam() {
    return team;
  }

  public PlayerRole getRole() {
    return role;
  }

  public Instant getJoinedAt() {
    return joinedAt;
  }
}
