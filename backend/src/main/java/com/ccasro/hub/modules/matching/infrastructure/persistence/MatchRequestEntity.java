package com.ccasro.hub.modules.matching.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "match_request")
@Getter
@Setter
public class MatchRequestEntity {

  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "organizer_id", nullable = false, columnDefinition = "uuid")
  private UUID organizerId;

  @Column(name = "resource_id", nullable = false, columnDefinition = "uuid")
  private UUID resourceId;

  @Column(name = "booking_date", nullable = false)
  private LocalDate bookingDate;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "slot_duration_minutes", nullable = false)
  private int slotDurationMinutes;

  @Column(nullable = false, length = 20)
  private String format;

  @Column(name = "skill_level", nullable = false, length = 20)
  private String skillLevel;

  @Column(name = "custom_message", columnDefinition = "TEXT")
  private String customMessage;

  @Column(name = "invitation_token", nullable = false, columnDefinition = "uuid")
  private UUID invitationToken;

  @Column(name = "search_lat", nullable = false)
  private double searchLat;

  @Column(name = "search_lng", nullable = false)
  private double searchLng;

  @Column(name = "search_radius_km", nullable = false)
  private double searchRadiusKm;

  @Column(nullable = false, length = 20)
  private String status;

  @Column(name = "price_per_player", precision = 10, scale = 2)
  private BigDecimal pricePerPlayer;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "matchRequest", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MatchPlayerEntity> players = new ArrayList<>();
}
