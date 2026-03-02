package com.ccasro.hub.modules.matching.domain;

public enum MatchFormat {
  ONE_VS_ONE(2),
  TWO_VS_TWO(4);

  private final int maxPlayers;

  MatchFormat(int maxPlayers) {
    this.maxPlayers = maxPlayers;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public int getPlayersPerTeam() {
    return maxPlayers / 2;
  }
}
