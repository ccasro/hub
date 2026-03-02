package com.ccasro.hub.modules.matching.domain.exception;

public class PlayerAlreadyJoinedException extends RuntimeException {
  public PlayerAlreadyJoinedException(String message) {
    super(message);
  }
}
