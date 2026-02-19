package com.ccasro.hub.shared.domain.security;

public enum UserRole {
  PLAYER,
  OWNER,
  ADMIN;

  public boolean isAdmin() {
    return this == ADMIN;
  }

  public boolean canManageVenues() {
    return this == OWNER || this == ADMIN;
  }

  public boolean isPlayer() {
    return this == PLAYER;
  }
}
