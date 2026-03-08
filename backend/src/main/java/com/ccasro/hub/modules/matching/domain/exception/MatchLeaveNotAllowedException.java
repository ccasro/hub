package com.ccasro.hub.modules.matching.domain.exception;

public class MatchLeaveNotAllowedException extends RuntimeException {
  public MatchLeaveNotAllowedException() {
    super("Cannot leave a match within 48 hours of the scheduled start time");
  }

  public MatchLeaveNotAllowedException(String message) {
    super(message);
  }
}
