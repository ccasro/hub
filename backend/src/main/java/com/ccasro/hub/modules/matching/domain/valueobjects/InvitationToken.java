package com.ccasro.hub.modules.matching.domain.valueobjects;

import java.util.UUID;

public record InvitationToken(UUID value) {
  public static InvitationToken generate() {
    return new InvitationToken(UUID.randomUUID());
  }

  public static InvitationToken of(UUID value) {
    return new InvitationToken(value);
  }
}
