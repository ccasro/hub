package com.ccasro.hub.modules.matching.domain.exception;

public class PlayerTimeConflictException extends RuntimeException {
  public PlayerTimeConflictException() {
    super("You already have an active match that overlaps with this time slot");
  }
}
