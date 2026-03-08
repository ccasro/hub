package com.ccasro.hub.modules.matching.domain;

import com.ccasro.hub.modules.matching.domain.exception.MatchNotOpenException;
import com.ccasro.hub.modules.matching.domain.exception.PlayerAlreadyJoinedException;
import com.ccasro.hub.modules.matching.domain.exception.TeamFullException;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import com.ccasro.hub.modules.matching.domain.valueobjects.InvitationToken;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchRequest {

  private static final int CLOSE_BEFORE_MATCH_HOURS = 24;

  private final MatchRequestId id;
  private final UserId organizerId;
  private final ResourceId resourceId;
  private final LocalDate bookingDate;
  private final LocalTime startTime;
  private final int slotDurationMinutes;
  private final MatchFormat format;
  private final MatchSkillLevel skillLevel;
  private final String customMessage;
  private final InvitationToken invitationToken;
  private final GeoPoint searchCenter;
  private final double searchRadiusKm;
  private final BigDecimal pricePerPlayer;
  private final Instant expiresAt;
  private final Instant createdAt;

  private MatchStatus status;
  private final List<MatchPlayer> players;

  public static MatchRequest create(
      UserId organizerId,
      ResourceId resourceId,
      LocalDate bookingDate,
      LocalTime startTime,
      int slotDurationMinutes,
      MatchFormat format,
      MatchSkillLevel skillLevel,
      String customMessage,
      GeoPoint searchCenter,
      double searchRadiusKm,
      BigDecimal pricePerPlayer,
      Clock clock) {

    List<MatchPlayer> players = new ArrayList<>();
    players.add(MatchPlayer.organizer(organizerId, clock));

    Instant now = clock.instant();
    Instant expiresAt =
        LocalDateTime.of(bookingDate, startTime)
            .minusHours(CLOSE_BEFORE_MATCH_HOURS)
            .toInstant(ZoneOffset.UTC);
    return new MatchRequest(
        MatchRequestId.generate(),
        organizerId,
        resourceId,
        bookingDate,
        startTime,
        slotDurationMinutes,
        format,
        skillLevel,
        customMessage,
        InvitationToken.generate(),
        searchCenter,
        searchRadiusKm,
        pricePerPlayer,
        MatchStatus.AWAITING_ORGANIZER_PAYMENT,
        players,
        expiresAt,
        now);
  }

  public static MatchRequest reconstitute(
      MatchRequestId id,
      UserId organizerId,
      ResourceId resourceId,
      LocalDate bookingDate,
      LocalTime startTime,
      int slotDurationMinutes,
      MatchFormat format,
      MatchSkillLevel skillLevel,
      String customMessage,
      InvitationToken invitationToken,
      GeoPoint searchCenter,
      double searchRadiusKm,
      BigDecimal pricePerPlayer,
      MatchStatus status,
      List<MatchPlayer> players,
      Instant expiresAt,
      Instant createdAt) {
    return new MatchRequest(
        id,
        organizerId,
        resourceId,
        bookingDate,
        startTime,
        slotDurationMinutes,
        format,
        skillLevel,
        customMessage,
        invitationToken,
        searchCenter,
        searchRadiusKm,
        pricePerPlayer,
        status,
        new ArrayList<>(players),
        expiresAt,
        createdAt);
  }

  private MatchRequest(
      MatchRequestId id,
      UserId organizerId,
      ResourceId resourceId,
      LocalDate bookingDate,
      LocalTime startTime,
      int slotDurationMinutes,
      MatchFormat format,
      MatchSkillLevel skillLevel,
      String customMessage,
      InvitationToken invitationToken,
      GeoPoint searchCenter,
      double searchRadiusKm,
      BigDecimal pricePerPlayer,
      MatchStatus status,
      List<MatchPlayer> players,
      Instant expiresAt,
      Instant createdAt) {
    this.id = id;
    this.organizerId = organizerId;
    this.resourceId = resourceId;
    this.bookingDate = bookingDate;
    this.startTime = startTime;
    this.slotDurationMinutes = slotDurationMinutes;
    this.format = format;
    this.skillLevel = skillLevel;
    this.customMessage = customMessage;
    this.invitationToken = invitationToken;
    this.searchCenter = searchCenter;
    this.searchRadiusKm = searchRadiusKm;
    this.pricePerPlayer = pricePerPlayer;
    this.status = status;
    this.players = players;
    this.expiresAt = expiresAt;
    this.createdAt = createdAt;
  }

  public void openForPlayers() {
    if (status != MatchStatus.AWAITING_ORGANIZER_PAYMENT)
      throw new IllegalStateException("Match is not awaiting organizer payment");
    status = MatchStatus.OPEN;
  }

  public void cancelDueToPaymentTimeout() {
    if (status != MatchStatus.AWAITING_ORGANIZER_PAYMENT)
      throw new IllegalStateException("Match is not awaiting organizer payment");
    status = MatchStatus.CANCELLED;
  }

  public boolean isAwaitingOrganizerPayment() {
    return status == MatchStatus.AWAITING_ORGANIZER_PAYMENT;
  }

  public void join(UserId playerId, PlayerTeam team, Clock clock) {
    if (status != MatchStatus.OPEN)
      throw new MatchNotOpenException("Match is not open for joining");

    if (players.stream().anyMatch(p -> p.getPlayerId().equals(playerId)))
      throw new PlayerAlreadyJoinedException("Player already joined this match");

    long teamCount = players.stream().filter(p -> p.getTeam() == team).count();
    if (teamCount >= format.getPlayersPerTeam())
      throw new TeamFullException("Team " + team + " is already full");

    players.add(MatchPlayer.guest(playerId, team, clock));

    if (players.size() == format.getMaxPlayers()) {
      status = MatchStatus.FULL;
    }
  }

  public void cancel() {
    if (status == MatchStatus.FULL)
      throw new IllegalStateException(
          "Match is full. To leave, use the leave or report-absence flow.");
    if (status == MatchStatus.CANCELLED)
      throw new IllegalStateException("Match is already cancelled");
    status = MatchStatus.CANCELLED;
  }

  public void removePlayer(UserId playerId) {
    boolean removed = players.removeIf(p -> p.getPlayerId().equals(playerId));
    if (removed && status == MatchStatus.FULL) {
      status = MatchStatus.OPEN;
    }
  }

  public void checkIn(UserId playerId, Clock clock) {
    players.stream()
        .filter(p -> p.getPlayerId().equals(playerId))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Player is not a participant of this match"))
        .checkIn(clock.instant());
  }

  public void reportAbsence(UserId playerId) {
    boolean removed = players.removeIf(p -> p.getPlayerId().equals(playerId));
    if (!removed) throw new IllegalStateException("Player is not a participant of this match");
    if (status == MatchStatus.FULL) {
      status = MatchStatus.OPEN;
    }
  }

  public void expire() {
    if (status == MatchStatus.OPEN) status = MatchStatus.EXPIRED;
  }

  public boolean isActive() {
    return status == MatchStatus.AWAITING_ORGANIZER_PAYMENT
        || status == MatchStatus.OPEN
        || status == MatchStatus.FULL;
  }

  public boolean isExpired(Clock clock) {
    return clock.instant().isAfter(expiresAt);
  }

  public boolean isFull() {
    return status == MatchStatus.FULL;
  }

  public boolean isOpen() {
    return status == MatchStatus.OPEN;
  }

  public int availableSlots() {
    return format.getMaxPlayers() - players.size();
  }

  public MatchRequestId getId() {
    return id;
  }

  public UserId getOrganizerId() {
    return organizerId;
  }

  public ResourceId getResourceId() {
    return resourceId;
  }

  public LocalDate getBookingDate() {
    return bookingDate;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public int getSlotDurationMinutes() {
    return slotDurationMinutes;
  }

  public MatchFormat getFormat() {
    return format;
  }

  public MatchSkillLevel getSkillLevel() {
    return skillLevel;
  }

  public String getCustomMessage() {
    return customMessage;
  }

  public InvitationToken getInvitationToken() {
    return invitationToken;
  }

  public GeoPoint getSearchCenter() {
    return searchCenter;
  }

  public double getSearchRadiusKm() {
    return searchRadiusKm;
  }

  public MatchStatus getStatus() {
    return status;
  }

  public List<MatchPlayer> getPlayers() {
    return Collections.unmodifiableList(players);
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public BigDecimal getPricePerPlayer() {
    return pricePerPlayer;
  }
}
