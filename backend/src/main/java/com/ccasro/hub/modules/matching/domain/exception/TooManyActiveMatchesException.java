package com.ccasro.hub.modules.matching.domain.exception;

public class TooManyActiveMatchesException extends RuntimeException {
  public TooManyActiveMatchesException() {
    super(
        "You already have the maximum number of active matches. Cancel one before creating a new one.");
  }
}
