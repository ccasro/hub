package com.ccasro.hub.modules.matching.domain.exception;

public class NotMatchOrganizerException extends RuntimeException {
  public NotMatchOrganizerException() {
    super("Only the match organizer can cancel this match");
  }

  public NotMatchOrganizerException(String message) {
    super(message);
  }
}
