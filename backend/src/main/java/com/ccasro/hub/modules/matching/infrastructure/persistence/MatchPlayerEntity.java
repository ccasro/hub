package com.ccasro.hub.modules.matching.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "match_player")
@Getter
@Setter
public class MatchPlayerEntity {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "match_request_id", nullable = false, columnDefinition = "uuid")
  private UUID matchRequestId;

  @Column(name = "player_id", nullable = false, columnDefinition = "uuid")
  private UUID playerId;

  @Column(nullable = false, length = 10)
  private String team;

  @Column(nullable = false, length = 20)
  private String role;

  @Column(name = "joined_at", nullable = false)
  private Instant joinedAt;
}
