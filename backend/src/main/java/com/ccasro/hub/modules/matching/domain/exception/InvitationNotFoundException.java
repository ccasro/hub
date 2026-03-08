package com.ccasro.hub.modules.matching.domain.exception;

public class InvitationNotFoundException extends RuntimeException {
  public InvitationNotFoundException() {
    super("Invitation not found");
  }
}
