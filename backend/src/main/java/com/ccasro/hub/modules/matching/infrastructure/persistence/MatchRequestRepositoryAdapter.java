package com.ccasro.hub.modules.matching.infrastructure.persistence;

import com.ccasro.hub.modules.matching.domain.*;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import com.ccasro.hub.modules.matching.domain.valueobjects.InvitationToken;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchRequestRepositoryAdapter implements MatchRequestRepositoryPort {

  private final MatchRequestJpaRepository jpaRepository;
  private final Clock clock;

  @Override
  public MatchRequest save(MatchRequest matchRequest) {
    MatchRequestEntity entity =
        jpaRepository.findById(matchRequest.getId().value()).orElseGet(MatchRequestEntity::new);

    updateEntity(entity, matchRequest);
    jpaRepository.save(entity);
    return matchRequest;
  }

  @Override
  public Optional<MatchRequest> findById(MatchRequestId id) {
    return jpaRepository.findById(id.value()).map(this::toDomain);
  }

  @Override
  public Optional<MatchRequest> findByInvitationToken(InvitationToken token) {
    return jpaRepository.findByInvitationToken(token.value()).map(this::toDomain);
  }

  @Override
  public List<MatchRequest> findByOrganizerId(UserId organizerId) {
    return jpaRepository.findByOrganizerId(organizerId.value()).stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public List<MatchRequest> findByPlayerId(UserId playerId) {
    return jpaRepository.findByPlayerId(playerId.value()).stream().map(this::toDomain).toList();
  }

  @Override
  public List<MatchRequest> findOpenAndExpired() {
    return jpaRepository.findOpenAndExpired(clock.instant()).stream().map(this::toDomain).toList();
  }

  @Override
  public Optional<MatchRequest> findActiveByResourceAndSlot(
      ResourceId resourceId, LocalDate date, LocalTime startTime) {
    return jpaRepository
        .findActiveByResourceAndSlot(resourceId.value(), date, startTime)
        .map(this::toDomain);
  }

  // ── Mappers ───────────────────────────────────────────────────

  private MatchRequest toDomain(MatchRequestEntity e) {
    List<MatchPlayer> players =
        e.getPlayers().stream()
            .map(
                p ->
                    MatchPlayer.reconstitute(
                        UserId.from(p.getPlayerId()),
                        PlayerTeam.valueOf(p.getTeam()),
                        PlayerRole.valueOf(p.getRole()),
                        p.getJoinedAt()))
            .toList();

    return MatchRequest.reconstitute(
        MatchRequestId.of(e.getId()),
        UserId.from(e.getOrganizerId()),
        ResourceId.of(e.getResourceId()),
        e.getBookingDate(),
        e.getStartTime(),
        e.getSlotDurationMinutes(),
        MatchFormat.valueOf(e.getFormat()),
        MatchSkillLevel.valueOf(e.getSkillLevel()),
        e.getCustomMessage(),
        InvitationToken.of(e.getInvitationToken()),
        new GeoPoint(e.getSearchLat(), e.getSearchLng()),
        e.getSearchRadiusKm(),
        MatchStatus.valueOf(e.getStatus()),
        players,
        e.getExpiresAt(),
        e.getCreatedAt());
  }

  private void updateEntity(MatchRequestEntity e, MatchRequest m) {
    e.setId(m.getId().value());
    e.setOrganizerId(m.getOrganizerId().value());
    e.setResourceId(m.getResourceId().value());
    e.setBookingDate(m.getBookingDate());
    e.setStartTime(m.getStartTime());
    e.setSlotDurationMinutes(m.getSlotDurationMinutes());
    e.setFormat(m.getFormat().name());
    e.setSkillLevel(m.getSkillLevel().name());
    e.setCustomMessage(m.getCustomMessage());
    e.setInvitationToken(m.getInvitationToken().value());
    e.setSearchLat(m.getSearchCenter().latitude());
    e.setSearchLng(m.getSearchCenter().longitude());
    e.setSearchRadiusKm(m.getSearchRadiusKm());
    e.setStatus(m.getStatus().name());
    e.setExpiresAt(m.getExpiresAt());
    e.setCreatedAt(m.getCreatedAt());

    Set<UUID> incomingPlayerIds =
        m.getPlayers().stream().map(p -> p.getPlayerId().value()).collect(Collectors.toSet());

    e.getPlayers().removeIf(pe -> !incomingPlayerIds.contains(pe.getPlayerId()));

    Set<UUID> existingPlayerIds =
        e.getPlayers().stream().map(MatchPlayerEntity::getPlayerId).collect(Collectors.toSet());

    m.getPlayers().stream()
        .filter(p -> !existingPlayerIds.contains(p.getPlayerId().value()))
        .forEach(
            p -> {
              MatchPlayerEntity pe = new MatchPlayerEntity();
              pe.setMatchRequestId(m.getId().value());
              pe.setPlayerId(p.getPlayerId().value());
              pe.setTeam(p.getTeam().name());
              pe.setRole(p.getRole().name());
              pe.setJoinedAt(p.getJoinedAt());
              e.getPlayers().add(pe);
            });
  }
}
