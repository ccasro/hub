package com.ccasro.hub.modules.matching.domain.exception;

public class InvitationNotYoursException extends RuntimeException {
  public InvitationNotYoursException() {
    super("This invitation is not yours");
  }
}
