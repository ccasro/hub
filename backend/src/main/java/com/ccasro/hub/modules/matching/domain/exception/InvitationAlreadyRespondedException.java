package com.ccasro.hub.modules.matching.domain.exception;

public class InvitationAlreadyRespondedException extends RuntimeException {
  public InvitationAlreadyRespondedException() {
    super("Invitation already responded");
  }
}
