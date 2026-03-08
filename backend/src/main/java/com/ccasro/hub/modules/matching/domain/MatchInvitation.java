package com.ccasro.hub.modules.matching.domain;

import java.time.Instant;
import java.util.UUID;

public class MatchInvitation {

  private final UUID id;
  private final UUID matchRequestId;
  private final UUID playerId;
  private final String playerEmail;
  private MatchInvitationStatus status;
  private final Instant sentAt;
  private Instant respondedAt;
  private final boolean freeSubstitute;

  private MatchInvitation(
      UUID id,
      UUID matchRequestId,
      UUID playerId,
      String playerEmail,
      MatchInvitationStatus status,
      Instant sentAt,
      Instant respondedAt,
      boolean freeSubstitute) {
    this.id = id;
    this.matchRequestId = matchRequestId;
    this.playerId = playerId;
    this.playerEmail = playerEmail;
    this.status = status;
    this.sentAt = sentAt;
    this.respondedAt = respondedAt;
    this.freeSubstitute = freeSubstitute;
  }

  public static MatchInvitation create(
      UUID matchRequestId, UUID playerId, String playerEmail, Instant now) {
    return new MatchInvitation(
        UUID.randomUUID(),
        matchRequestId,
        playerId,
        playerEmail,
        MatchInvitationStatus.PENDING,
        now,
        null,
        false);
  }

  public static MatchInvitation createFreeSubstitute(
      UUID matchRequestId, UUID playerId, String playerEmail, Instant now) {
    return new MatchInvitation(
        UUID.randomUUID(),
        matchRequestId,
        playerId,
        playerEmail,
        MatchInvitationStatus.PENDING,
        now,
        null,
        true);
  }

  public static MatchInvitation reconstitute(
      UUID id,
      UUID matchRequestId,
      UUID playerId,
      String playerEmail,
      MatchInvitationStatus status,
      Instant sentAt,
      Instant respondedAt,
      boolean freeSubstitute) {
    return new MatchInvitation(
        id, matchRequestId, playerId, playerEmail, status, sentAt, respondedAt, freeSubstitute);
  }

  public void accept(Instant now) {
    this.status = MatchInvitationStatus.ACCEPTED;
    this.respondedAt = now;
  }

  public void decline(Instant now) {
    this.status = MatchInvitationStatus.DECLINED;
    this.respondedAt = now;
  }

  public void expire(Instant now) {
    if (this.status == MatchInvitationStatus.PENDING) {
      this.status = MatchInvitationStatus.EXPIRED;
      this.respondedAt = now;
    }
  }

  public boolean isPending() {
    return this.status == MatchInvitationStatus.PENDING;
  }

  public UUID getId() {
    return id;
  }

  public UUID getMatchRequestId() {
    return matchRequestId;
  }

  public UUID getPlayerId() {
    return playerId;
  }

  public String getPlayerEmail() {
    return playerEmail;
  }

  public MatchInvitationStatus getStatus() {
    return status;
  }

  public Instant getSentAt() {
    return sentAt;
  }

  public Instant getRespondedAt() {
    return respondedAt;
  }

  public boolean isFreeSubstitute() {
    return freeSubstitute;
  }
}
