package com.ccasro.hub.modules.matching.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "match_invitation")
@Getter
@Setter
public class MatchInvitationEntity {

  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "match_request_id", nullable = false, columnDefinition = "uuid")
  private UUID matchRequestId;

  @Column(name = "player_id", nullable = false, columnDefinition = "uuid")
  private UUID playerId;

  @Column(name = "player_email", nullable = false, length = 255)
  private String playerEmail;

  @Column(nullable = false, length = 20)
  private String status;

  @Column(name = "sent_at", nullable = false)
  private Instant sentAt;

  @Column(name = "responded_at")
  private Instant respondedAt;

  @Column(name = "free_substitute", nullable = false)
  private boolean freeSubstitute;
}
