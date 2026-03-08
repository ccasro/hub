package com.ccasro.hub.modules.matching.domain.exception;

public class PlayerMatchBannedException extends RuntimeException {
  public PlayerMatchBannedException() {
    super("You are temporarily banned from joining matches due to repeated no-shows");
  }
}
