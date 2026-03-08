package com.ccasro.hub.modules.match;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ccasro.hub.BaseIT;
import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.matching.domain.MatchInvitation;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchInvitationRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class MatchIT extends BaseIT {

  @org.springframework.beans.factory.annotation.Autowired
  private MatchInvitationRepositoryPort invitations;

  private static final String RESOURCE_ID = "20000000-0000-0000-0000-000000000001";
  private static final LocalDate MATCH_DATE = nextMonday();
  private static final String SLOT_TIME = "09:30:00";

  @BeforeEach
  void setupUsers() {
    givenPlayer(); // auth0|user       → PLAYER_TOKEN
    givenPlayer2(); // auth0|player2    → PLAYER2_TOKEN
    givenOwner(); // auth0|owner      → OWNER_TOKEN  (para test equipo lleno)
  }

  // ── GET /api/match/search ─────────────────────────────────────────────────

  @Test
  void search_sin_token_devuelve_401() throws Exception {
    mvc.perform(
            get("/api/match/search")
                .param("lat", "40.4168")
                .param("lng", "-3.7038")
                .param("date", MATCH_DATE.toString())
                .param("startTimeFrom", "08:00")
                .param("startTimeTo", "22:00"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void search_devuelve_slots_del_venue_en_madrid() throws Exception {
    mvc.perform(
            get("/api/match/search")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .param("lat", "40.4168")
                .param("lng", "-3.7038")
                .param("radiusKm", "10")
                .param("date", MATCH_DATE.toString())
                .param("startTimeFrom", "08:00")
                .param("startTimeTo", "22:00")
                .param("slotDurationMinutes", "90")
                .param("format", "TWO_VS_TWO")
                .param("skillLevel", "ANY"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].venueCity").value("Madrid"))
        .andExpect(jsonPath("$[0].currency").value("EUR"));
  }

  @Test
  void search_coordenadas_sin_venues_devuelve_lista_vacia() throws Exception {
    // Islandia — ningún venue del seed está cerca
    mvc.perform(
            get("/api/match/search")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .param("lat", "64.9631")
                .param("lng", "-19.0208")
                .param("radiusKm", "10")
                .param("date", MATCH_DATE.toString())
                .param("startTimeFrom", "08:00")
                .param("startTimeTo", "22:00")
                .param("slotDurationMinutes", "90")
                .param("format", "TWO_VS_TWO")
                .param("skillLevel", "ANY"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void search_sin_date_devuelve_400() throws Exception {
    mvc.perform(
            get("/api/match/search")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .param("lat", "40.4168")
                .param("lng", "-3.7038")
                .param("startTimeFrom", "08:00")
                .param("startTimeTo", "22:00"))
        .andExpect(status().isBadRequest());
  }

  // ── POST /api/match/requests ──────────────────────────────────────────────

  @Test
  void create_sin_token_devuelve_401() throws Exception {
    mvc.perform(
            post("/api/match/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateBody(SLOT_TIME)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void create_devuelve_201_en_estado_awaiting_payment() throws Exception {
    mvc.perform(
            post("/api/match/requests")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateBody(SLOT_TIME)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("AWAITING_ORGANIZER_PAYMENT"))
        .andExpect(jsonPath("$.format").value("TWO_VS_TWO"))
        .andExpect(jsonPath("$.skillLevel").value("ANY"))
        .andExpect(jsonPath("$.players[0].role").value("ORGANIZER"))
        .andExpect(jsonPath("$.invitationToken").isNotEmpty());
  }

  @Test
  void create_slot_fuera_de_horario_devuelve_409() throws Exception {
    // 23:00 fuera del schedule MON 08:00-22:00
    // GlobalExceptionHandler mapea SlotNotAvailableException → 409 Conflict
    mvc.perform(
            post("/api/match/requests")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateBody("23:00:00")))
        .andExpect(status().isConflict());
  }

  @Test
  void create_slot_ya_ocupado_devuelve_409() throws Exception {
    mvc.perform(
            post("/api/match/requests")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateBody(SLOT_TIME)))
        .andExpect(status().isCreated());

    mvc.perform(
            post("/api/match/requests")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateBody(SLOT_TIME)))
        .andExpect(status().isConflict());
  }

  // ── GET /api/match/requests/{id} ──────────────────────────────────────────

  @Test
  void get_by_id_sin_token_devuelve_401() throws Exception {
    mvc.perform(get("/api/match/requests/40000000-0000-0000-0000-000000000001"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void get_by_id_devuelve_200() throws Exception {
    String token = createMatchAndGetToken();
    String id = getIdFromToken(token);

    mvc.perform(get("/api/match/requests/{id}", id).header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.status").value("AWAITING_ORGANIZER_PAYMENT"));
  }

  @Test
  void get_by_id_inexistente_devuelve_404() throws Exception {
    mvc.perform(
            get("/api/match/requests/00000000-0000-0000-0000-000000000099")
                .header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isNotFound());
  }

  // ── GET /api/match/join/{token} ───────────────────────────────────────────

  @Test
  void get_by_token_sin_token_devuelve_401() throws Exception {
    mvc.perform(get("/api/match/join/60000000-0000-0000-0000-000000000001"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void get_by_token_devuelve_200() throws Exception {
    String token = createMatchAndGetToken();

    mvc.perform(get("/api/match/join/{token}", token).header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.invitationToken").value(token))
        .andExpect(jsonPath("$.status").value("AWAITING_ORGANIZER_PAYMENT"));
  }

  @Test
  void get_by_token_inexistente_devuelve_404() throws Exception {
    mvc.perform(
            get("/api/match/join/00000000-0000-0000-0000-000000000099")
                .header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isNotFound());
  }

  // ── POST /api/match/join/{token} ──────────────────────────────────────────

  @Test
  void join_sin_token_devuelve_401() throws Exception {
    mvc.perform(
            post("/api/match/join/60000000-0000-0000-0000-000000000001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"team": "TEAM_2"}
                """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void join_devuelve_200_con_slot_reducido() throws Exception {
    String token = createOpenMatchAndGetToken();

    mvc.perform(
            post("/api/match/join/{token}", token)
                .header("Authorization", bearer(PLAYER2_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"team": "TEAM_2"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("OPEN"))
        .andExpect(jsonPath("$.availableSlots").value(2));
  }

  @Test
  void join_jugador_duplicado_devuelve_422() throws Exception {
    String token = createOpenMatchAndGetToken();

    mvc.perform(
            post("/api/match/join/{token}", token)
                .header("Authorization", bearer(PLAYER2_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"team": "TEAM_2"}
                """))
        .andExpect(status().isOk());

    // mismo jugador intenta unirse de nuevo
    mvc.perform(
            post("/api/match/join/{token}", token)
                .header("Authorization", bearer(PLAYER2_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"team": "TEAM_1"}
                """))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void join_equipo_lleno_devuelve_422() throws Exception {
    String token = createOpenMatchAndGetToken();

    mvc.perform(
            post("/api/match/join/{token}", token)
                .header("Authorization", bearer(PLAYER2_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"team": "TEAM_1"}
                """))
        .andExpect(status().isOk());

    mvc.perform(
            post("/api/match/join/{token}", token)
                .header("Authorization", bearer(OWNER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"team": "TEAM_1"}
                """))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void join_token_inexistente_devuelve_404() throws Exception {
    mvc.perform(
            post("/api/match/join/00000000-0000-0000-0000-000000000099")
                .header("Authorization", bearer(PLAYER2_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"team": "TEAM_2"}
                """))
        .andExpect(status().isNotFound());
  }

  // ── DELETE /api/match/requests/{id} ──────────────────────────────────────

  @Test
  void cancel_sin_token_devuelve_401() throws Exception {
    mvc.perform(delete("/api/match/requests/00000000-0000-0000-0000-000000000001"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void cancel_devuelve_204() throws Exception {
    String token = createMatchAndGetToken();
    String id = getIdFromToken(token);

    mvc.perform(
            delete("/api/match/requests/{id}", id).header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isNoContent());
  }

  @Test
  void cancel_no_organizer_devuelve_403() throws Exception {
    String token = createMatchAndGetToken();
    String id = getIdFromToken(token);

    mvc.perform(
            delete("/api/match/requests/{id}", id).header("Authorization", bearer(PLAYER2_TOKEN)))
        .andExpect(status().isForbidden());
  }

  // ── Límite de partidos activos ────────────────────────────────────────────

  @Test
  void create_mas_de_2_activos_devuelve_422() throws Exception {
    mvc.perform(
            post("/api/match/requests")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateBody("09:30:00")))
        .andExpect(status().isCreated());

    mvc.perform(
            post("/api/match/requests")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateBody("11:00:00")))
        .andExpect(status().isCreated());

    mvc.perform(
            post("/api/match/requests")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateBody("12:30:00")))
        .andExpect(status().isUnprocessableEntity());
  }

  // ── Cooldown tras cancelación ─────────────────────────────────────────────

  @Test
  void create_en_cooldown_devuelve_422() throws Exception {
    UserProfile player = givenPlayer();

    // Forzar cooldown: simular que el jugador canceló hace instantes.
    // cooldownThreshold = ahora-1s → la condición IS NULL se cumple → escribe
    // last_match_cancelled_at = now.
    // getCooldownHoursRemaining verá ~24h restantes.
    users.tryRecordMatchCancellation(player.getId(), Instant.now(), Instant.now().minusSeconds(1));

    mvc.perform(
            post("/api/match/requests")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildCreateBody(SLOT_TIME)))
        .andExpect(status().isUnprocessableEntity());
  }

  // ── GET /api/match/requests/my ────────────────────────────────────────────

  @Test
  void my_matches_sin_token_devuelve_401() throws Exception {
    mvc.perform(get("/api/match/requests/my")).andExpect(status().isUnauthorized());
  }

  @Test
  void my_matches_devuelve_partido_creado() throws Exception {
    createMatchAndGetToken();

    mvc.perform(get("/api/match/requests/my").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].status").value("AWAITING_ORGANIZER_PAYMENT"));
  }

  // ── GET /api/match/invitations ────────────────────────────────────────────

  @Test
  void invitations_sin_token_devuelve_401() throws Exception {
    mvc.perform(get("/api/match/invitations")).andExpect(status().isUnauthorized());
  }

  @Test
  void invitations_devuelve_lista_vacia_sin_invitaciones() throws Exception {
    mvc.perform(get("/api/match/invitations").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  private String getIdFromToken(String token) throws Exception {
    MvcResult result =
        mvc.perform(
                get("/api/match/join/{token}", token).header("Authorization", bearer(PLAYER_TOKEN)))
            .andExpect(status().isOk())
            .andReturn();

    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .readTree(result.getResponse().getContentAsString())
        .get("id")
        .asText();
  }

  // ── POST /api/match/invitations/{id}/accept ───────────────────────────────

  @Test
  void accept_invitation_sin_token_devuelve_401() throws Exception {
    mvc.perform(
            post("/api/match/invitations/00000000-0000-0000-0000-000000000001/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"team\": \"TEAM_2\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void accept_invitation_devuelve_200_y_jugador_unido() throws Exception {
    String token = createOpenMatchAndGetToken();
    String matchId = getIdFromToken(token);
    UserProfile player2 = givenPlayer2();

    MatchInvitation inv =
        MatchInvitation.create(
            java.util.UUID.fromString(matchId),
            player2.getId().value(),
            player2.getEmail().value(),
            Instant.now());
    invitations.save(inv);

    mvc.perform(
            post("/api/match/invitations/{id}/accept", inv.getId())
                .header("Authorization", bearer(PLAYER2_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"team\": \"TEAM_2\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("OPEN"))
        .andExpect(jsonPath("$.availableSlots").value(2));
  }

  @Test
  void accept_invitation_ajena_devuelve_403() throws Exception {
    String token = createOpenMatchAndGetToken();
    String matchId = getIdFromToken(token);
    UserProfile player2 = givenPlayer2();

    MatchInvitation inv =
        MatchInvitation.create(
            java.util.UUID.fromString(matchId),
            player2.getId().value(),
            player2.getEmail().value(),
            Instant.now());
    invitations.save(inv);

    // PLAYER_TOKEN intenta aceptar la invitación de PLAYER2
    mvc.perform(
            post("/api/match/invitations/{id}/accept", inv.getId())
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"team\": \"TEAM_2\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  void accept_invitation_ya_respondida_devuelve_422() throws Exception {
    String token = createOpenMatchAndGetToken();
    String matchId = getIdFromToken(token);
    UserProfile player2 = givenPlayer2();

    MatchInvitation inv =
        MatchInvitation.create(
            java.util.UUID.fromString(matchId),
            player2.getId().value(),
            player2.getEmail().value(),
            Instant.now());
    invitations.save(inv);

    // Primera respuesta (decline)
    mvc.perform(
            post("/api/match/invitations/{id}/decline", inv.getId())
                .header("Authorization", bearer(PLAYER2_TOKEN)))
        .andExpect(status().isNoContent());

    // Intento de aceptar tras ya haber respondido
    mvc.perform(
            post("/api/match/invitations/{id}/accept", inv.getId())
                .header("Authorization", bearer(PLAYER2_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"team\": \"TEAM_2\"}"))
        .andExpect(status().isUnprocessableEntity());
  }

  // ── POST /api/match/invitations/{id}/decline ──────────────────────────────

  @Test
  void decline_invitation_sin_token_devuelve_401() throws Exception {
    mvc.perform(post("/api/match/invitations/00000000-0000-0000-0000-000000000001/decline"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void decline_invitation_devuelve_204() throws Exception {
    String token = createOpenMatchAndGetToken();
    String matchId = getIdFromToken(token);
    UserProfile player2 = givenPlayer2();

    MatchInvitation inv =
        MatchInvitation.create(
            java.util.UUID.fromString(matchId),
            player2.getId().value(),
            player2.getEmail().value(),
            Instant.now());
    invitations.save(inv);

    mvc.perform(
            post("/api/match/invitations/{id}/decline", inv.getId())
                .header("Authorization", bearer(PLAYER2_TOKEN)))
        .andExpect(status().isNoContent());
  }

  // ── DELETE /api/match/requests/{id}/leave ────────────────────────────────

  @Test
  void leave_sin_token_devuelve_401() throws Exception {
    mvc.perform(delete("/api/match/requests/00000000-0000-0000-0000-000000000001/leave"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void leave_devuelve_204_y_slot_recuperado() throws Exception {
    String token = createOpenMatchAndGetToken();
    String matchId = getIdFromToken(token);

    // PLAYER2 se une al partido
    mvc.perform(
            post("/api/match/join/{token}", token)
                .header("Authorization", bearer(PLAYER2_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"team\": \"TEAM_2\"}"))
        .andExpect(status().isOk());

    // PLAYER2 abandona
    mvc.perform(
            delete("/api/match/requests/{id}/leave", matchId)
                .header("Authorization", bearer(PLAYER2_TOKEN)))
        .andExpect(status().isNoContent());

    // El slot vuelve a estar disponible
    mvc.perform(
            get("/api/match/requests/{id}", matchId).header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.availableSlots").value(3));
  }

  @Test
  void leave_organizador_partido_abierto_devuelve_403() throws Exception {
    String token = createOpenMatchAndGetToken();
    String matchId = getIdFromToken(token);

    mvc.perform(
            delete("/api/match/requests/{id}/leave", matchId)
                .header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isForbidden());
  }

  @Test
  void leave_no_participante_devuelve_409() throws Exception {
    String token = createOpenMatchAndGetToken();
    String matchId = getIdFromToken(token);

    // PLAYER2 nunca se unió — intenta abandonar
    mvc.perform(
            delete("/api/match/requests/{id}/leave", matchId)
                .header("Authorization", bearer(PLAYER2_TOKEN)))
        .andExpect(status().isConflict());
  }

  /** Creates a match and confirms organizer payment so it transitions to OPEN. */
  private String createOpenMatchAndGetToken() throws Exception {
    String token = createMatchAndGetToken();
    String id = getIdFromToken(token);
    mvc.perform(
            post("/api/match/requests/{id}/confirm-payment", id)
                .header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk());
    return token;
  }

  private String createMatchAndGetToken() throws Exception {
    MvcResult result =
        mvc.perform(
                post("/api/match/requests")
                    .header("Authorization", bearer(PLAYER_TOKEN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(buildCreateBody(SLOT_TIME)))
            .andExpect(status().isCreated())
            .andReturn();

    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .readTree(result.getResponse().getContentAsString())
        .get("invitationToken")
        .asText();
  }

  private String buildCreateBody(String startTime) {
    return buildCreateBodyForDate(startTime, MATCH_DATE);
  }

  private String buildCreateBodyForDate(String startTime, LocalDate date) {
    return """
        {
          "resourceId": "%s",
          "bookingDate": "%s",
          "startTime": "%s",
          "slotDurationMinutes": 90,
          "format": "TWO_VS_TWO",
          "skillLevel": "ANY",
          "customMessage": "Buscamos rival",
          "searchLat": 40.4168,
          "searchLng": -3.7038,
          "searchRadiusKm": 10.0
        }
        """
        .formatted(RESOURCE_ID, date.format(DateTimeFormatter.ISO_DATE), startTime);
  }

  private static LocalDate nextMonday() {
    // Start from at least 7 days ahead so the 48h-before-start check always passes
    LocalDate base = LocalDate.now().plusDays(7);
    int days = (8 - base.getDayOfWeek().getValue()) % 7;
    return base.plusDays(days == 0 ? 7 : days);
  }
}
