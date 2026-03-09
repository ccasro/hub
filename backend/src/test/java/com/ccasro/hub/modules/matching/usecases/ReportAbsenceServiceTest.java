package com.ccasro.hub.modules.matching.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.*;
import com.ccasro.hub.modules.matching.domain.exception.MatchNotFoundException;
import com.ccasro.hub.modules.matching.domain.ports.out.EligiblePlayerPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchNotificationPort;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import com.ccasro.hub.modules.matching.domain.valueobjects.InvitationToken;
import com.ccasro.hub.modules.matching.domain.valueobjects.MatchRequestId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportAbsenceServiceTest {

  private static final Instant NOW = Instant.parse("2026-03-10T10:00:00Z");
  private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

  // Dependencias mockeadas
  private MatchRequestRepositoryPort matchRepository;
  private MatchInvitationRepositoryPort invitationRepository;
  private MatchPlayerPaymentService matchPlayerPaymentService;
  private EligiblePlayerPort eligiblePlayerPort;
  private UserProfileRepositoryPort userRepository;
  private MatchNotificationPort notificationPort;
  private CurrentUserProvider currentUser;

  private ReportAbsenceService service;

  // IDs reutilizables
  private final UUID matchId = UUID.randomUUID();
  private final UserId organizerId = UserId.newId();
  private final UserId guestId = UserId.newId();

  @BeforeEach
  void setUp() {
    matchRepository = mock(MatchRequestRepositoryPort.class);
    invitationRepository = mock(MatchInvitationRepositoryPort.class);
    matchPlayerPaymentService = mock(MatchPlayerPaymentService.class);
    eligiblePlayerPort = mock(EligiblePlayerPort.class);
    userRepository = mock(UserProfileRepositoryPort.class);
    notificationPort = mock(MatchNotificationPort.class);
    currentUser = mock(CurrentUserProvider.class);

    service =
        new ReportAbsenceService(
            matchRepository,
            invitationRepository,
            matchPlayerPaymentService,
            eligiblePlayerPort,
            userRepository,
            notificationPort,
            currentUser,
            CLOCK);

    when(currentUser.getUserId()).thenReturn(guestId);
    when(userRepository.findEmailsByIds(any())).thenReturn(Map.of());
  }

  // ── Happy path: ausencia en partido FULL ────────────────────────────────────

  @Test
  void execute_ausencia_en_partido_full_llama_a_revertBooking_y_manda_invitaciones() {
    MatchRequest match = fullMatch(matchId, organizerId, guestId);
    when(matchRepository.findById(new MatchRequestId(matchId))).thenReturn(Optional.of(match));
    when(invitationRepository.findByMatchRequestId(matchId)).thenReturn(List.of());

    UUID sub1 = UUID.randomUUID();
    EligiblePlayerPort.EligiblePlayer eligible =
        new EligiblePlayerPort.EligiblePlayer(
            sub1.toString(), "sub1@test.com", "Sub 1", "ANY", new GeoPoint(40.4, -3.7), true);
    when(eligiblePlayerPort.findEligiblePlayers(any(), anyDouble(), any(), anyString()))
        .thenReturn(List.of(eligible));

    service.execute(matchId);

    // El partido debe haber cambiado a OPEN
    assertThat(match.isOpen()).isTrue();
    // Se guardó el partido
    verify(matchRepository).save(match);
    // Se revirtió el booking (partido era FULL)
    verify(matchPlayerPaymentService).revertBookingToPendingMatch(match);
    // Se creó la invitación de sustituto
    verify(invitationRepository).saveAll(argThat(list -> list.size() == 1));
  }

  // ── Bug fix: jugadores ya invitados deben ser excluidos ─────────────────────

  @Test
  void execute_excluye_jugadores_ya_invitados_previamente() {
    MatchRequest match = fullMatch(matchId, organizerId, guestId);
    when(matchRepository.findById(new MatchRequestId(matchId))).thenReturn(Optional.of(match));

    // Dos players elegibles: subA fue invitado antes (regular), subB es nuevo
    UUID subA = UUID.randomUUID();
    UUID subB = UUID.randomUUID();

    MatchInvitation existingInvitation =
        MatchInvitation.reconstitute(
            UUID.randomUUID(),
            matchId,
            subA,
            "suba@test.com",
            MatchInvitationStatus.PENDING,
            NOW.minusSeconds(60),
            null,
            false);
    when(invitationRepository.findByMatchRequestId(matchId))
        .thenReturn(List.of(existingInvitation));

    EligiblePlayerPort.EligiblePlayer eligibleA =
        new EligiblePlayerPort.EligiblePlayer(
            subA.toString(), "suba@test.com", "Sub A", "ANY", new GeoPoint(40.4, -3.7), true);
    EligiblePlayerPort.EligiblePlayer eligibleB =
        new EligiblePlayerPort.EligiblePlayer(
            subB.toString(), "subb@test.com", "Sub B", "ANY", new GeoPoint(40.4, -3.7), true);
    when(eligiblePlayerPort.findEligiblePlayers(any(), anyDouble(), any(), anyString()))
        .thenReturn(List.of(eligibleA, eligibleB));

    service.execute(matchId);

    // Solo debe crearse la invitación para subB (subA ya tiene una)
    verify(invitationRepository)
        .saveAll(
            argThat(
                list ->
                    list.size() == 1
                        && list.get(0).getPlayerId().equals(subB)
                        && list.get(0).isFreeSubstitute()));
  }

  @Test
  void execute_no_crea_ninguna_invitacion_si_todos_los_elegibles_ya_fueron_invitados() {
    MatchRequest match = fullMatch(matchId, organizerId, guestId);
    when(matchRepository.findById(new MatchRequestId(matchId))).thenReturn(Optional.of(match));

    UUID subA = UUID.randomUUID();
    MatchInvitation existing =
        MatchInvitation.reconstitute(
            UUID.randomUUID(),
            matchId,
            subA,
            "suba@test.com",
            MatchInvitationStatus.DECLINED,
            NOW.minusSeconds(60),
            NOW.minusSeconds(30),
            false);
    when(invitationRepository.findByMatchRequestId(matchId)).thenReturn(List.of(existing));

    EligiblePlayerPort.EligiblePlayer eligibleA =
        new EligiblePlayerPort.EligiblePlayer(
            subA.toString(), "suba@test.com", "Sub A", "ANY", new GeoPoint(40.4, -3.7), true);
    when(eligiblePlayerPort.findEligiblePlayers(any(), anyDouble(), any(), anyString()))
        .thenReturn(List.of(eligibleA));

    service.execute(matchId);

    // No se deben crear invitaciones
    verify(invitationRepository, never()).saveAll(any());
  }

  // ── Error: partido no encontrado ────────────────────────────────────────────

  @Test
  void execute_lanza_excepcion_si_partido_no_existe() {
    when(matchRepository.findById(new MatchRequestId(matchId))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.execute(matchId)).isInstanceOf(MatchNotFoundException.class);
  }

  // ── Error: jugador no es participante ───────────────────────────────────────

  @Test
  void execute_lanza_excepcion_si_el_jugador_no_esta_en_el_partido() {
    UserId anotherPlayer = UserId.newId();
    when(currentUser.getUserId()).thenReturn(anotherPlayer);

    MatchRequest match = fullMatch(matchId, organizerId, guestId);
    when(matchRepository.findById(new MatchRequestId(matchId))).thenReturn(Optional.of(match));

    assertThatThrownBy(() -> service.execute(matchId))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("not a participant");
  }

  // ── Helper ──────────────────────────────────────────────────────────────────

  /** Crea un MatchRequest en estado FULL con organizerId en TEAM_1 y guestId en TEAM_2 (1v1). */
  private static MatchRequest fullMatch(UUID matchId, UserId organizerId, UserId guestId) {
    List<MatchPlayer> players =
        List.of(
            MatchPlayer.reconstitute(
                organizerId,
                PlayerTeam.TEAM_1,
                PlayerRole.ORGANIZER,
                NOW.minusSeconds(300),
                false,
                null,
                null,
                null),
            MatchPlayer.reconstitute(
                guestId,
                PlayerTeam.TEAM_2,
                PlayerRole.GUEST,
                NOW.minusSeconds(200),
                false,
                null,
                null,
                null));

    return MatchRequest.reconstitute(
        new MatchRequestId(matchId),
        organizerId,
        ResourceId.generate(),
        LocalDate.of(2026, 3, 20),
        LocalTime.of(10, 0),
        90,
        MatchFormat.ONE_VS_ONE,
        MatchSkillLevel.ANY,
        null,
        InvitationToken.generate(),
        new GeoPoint(40.4168, -3.7038),
        10.0,
        BigDecimal.valueOf(10),
        MatchStatus.FULL,
        players,
        NOW.plusSeconds(3600),
        NOW.minusSeconds(600));
  }
}
