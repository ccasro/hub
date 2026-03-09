package com.ccasro.hub.modules.matching.domain.exception;

public class PlayerAlreadyLeftException extends RuntimeException {
  public PlayerAlreadyLeftException(String message) {
    super(message);
  }
}
