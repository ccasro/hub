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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "match_request_id", nullable = false)
  private MatchRequestEntity matchRequest;

  @Column(name = "player_id", nullable = false, columnDefinition = "uuid")
  private UUID playerId;

  @Column(nullable = false, length = 10)
  private String team;

  @Column(nullable = false, length = 20)
  private String role;

  @Column(name = "joined_at", nullable = false)
  private Instant joinedAt;

  @Column(name = "checked_in", nullable = false)
  private boolean checkedIn;

  @Column(name = "checked_in_at")
  private Instant checkedInAt;

  @Column(name = "left_at")
  private Instant leftAt;

  @Column(name = "left_reason", length = 20)
  private String leftReason;
}
