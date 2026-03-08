package com.ccasro.hub.modules.matching.domain.exception;

public class MatchCreationCooldownException extends RuntimeException {
  public MatchCreationCooldownException(long hoursRemaining) {
    super(
        "You must wait "
            + hoursRemaining
            + " more hour(s) after cancelling a match before creating a new one.");
  }
}
