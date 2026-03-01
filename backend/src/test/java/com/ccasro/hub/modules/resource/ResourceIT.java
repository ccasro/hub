package com.ccasro.hub.modules.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ccasro.hub.BaseIT;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class ResourceIT extends BaseIT {

  private String venueId;
  private String resourceId;

  @BeforeEach
  void setup() throws Exception {
    givenOwner();
    givenAdmin();

    String venueResponse =
        mvc.perform(
                post("/api/owner/venues")
                    .header("Authorization", bearer(OWNER_TOKEN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                {
                  "name": "Venue Resources Test", "street": "Carrer 1",
                  "city": "Barcelona", "country": "España",
                  "postalCode": "08001", "latitude": 41.3851, "longitude": 2.1734
                }
                """))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    venueId = JsonPath.read(venueResponse, "$.id");

    mvc.perform(
            patch("/api/admin/venues/" + venueId + "/approve")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk());

    String resourceResponse =
        mvc.perform(
                post("/api/owner/venues/" + venueId + "/resources")
                    .header("Authorization", bearer(OWNER_TOKEN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {"name": "Pista 1", "type": "PADEL", "slotDurationMinutes": 90}
                    """))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    resourceId = JsonPath.read(resourceResponse, "$.id");
  }

  @Test
  void crear_resource_sin_token_devuelve_401() throws Exception {
    mvc.perform(
            post("/api/owner/venues/" + venueId + "/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void crear_resource_como_player_devuelve_403() throws Exception {
    givenPlayer();

    mvc.perform(
            post("/api/owner/venues/" + venueId + "/resources")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"name": "Pista Player", "type": "PADEL", "slotDurationMinutes": 90}
                """))
        .andExpect(status().isForbidden());
  }

  @Test
  void resource_creado_tiene_status_pending() throws Exception {
    mvc.perform(get("/api/owner/resources").header("Authorization", bearer(OWNER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.id == '" + resourceId + "')].status").value("PENDING_REVIEW"));
  }

  @Test
  void aprobar_resource_cambia_status_a_active() throws Exception {
    mvc.perform(
            patch("/api/admin/resources/" + resourceId + "/approve")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void rechazar_resource_cambia_status_a_rejected() throws Exception {
    mvc.perform(
            patch("/api/admin/resources/" + resourceId + "/reject")
                .header("Authorization", bearer(ADMIN_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"reason": "No cumple los requisitos"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REJECTED"));
  }

  @Test
  void add_schedule_to_resource() throws Exception {
    mvc.perform(
            put("/api/owner/resources/" + resourceId + "/schedules")
                .header("Authorization", bearer(OWNER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"dayOfWeek": "MON", "openingTime": "09:00", "closingTime": "22:00"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.schedules").isArray());
  }

  @Test
  void add_price_rule_to_resource() throws Exception {
    mvc.perform(
            post("/api/owner/resources/" + resourceId + "/price-rules")
                .header("Authorization", bearer(OWNER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                  "dayType": "WEEKDAY",
                  "startTime": "09:00",
                  "endTime": "22:00",
                  "price": 18.00,
                  "currency": "EUR"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.priceRules").isArray())
        .andExpect(jsonPath("$.priceRules[0].price").value(18.0));
  }

  @Test
  void listar_resources_publicos_de_venue() throws Exception {
    mvc.perform(
            patch("/api/admin/resources/" + resourceId + "/approve")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk());

    mvc.perform(
            get("/api/venues/" + venueId + "/resources")
                .header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void slots_disponibles_resource_activo() throws Exception {
    // Aprovar + afegir schedule
    mvc.perform(
            patch("/api/admin/resources/" + resourceId + "/approve")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk());

    mvc.perform(
            put("/api/owner/resources/" + resourceId + "/schedules")
                .header("Authorization", bearer(OWNER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"dayOfWeek": "MON", "openingTime": "09:00", "closingTime": "22:00"}
                """))
        .andExpect(status().isOk());

    String monday = nextMonday().toString();
    mvc.perform(
            get("/api/resources/" + resourceId + "/slots")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .param("date", monday))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].startTime").exists())
        .andExpect(jsonPath("$[0].available").exists());
  }

  @Test
  void owner_puede_suspender_resource() throws Exception {
    mvc.perform(
            patch("/api/admin/resources/" + resourceId + "/approve")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk());

    mvc.perform(
            patch("/api/owner/resources/" + resourceId + "/suspend")
                .header("Authorization", bearer(OWNER_TOKEN)))
        .andExpect(status().isNoContent());
  }

  @Test
  void owner_puede_ver_sus_resources() throws Exception {
    mvc.perform(get("/api/owner/resources").header("Authorization", bearer(OWNER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  private java.time.LocalDate nextMonday() {
    java.time.LocalDate d = java.time.LocalDate.now().plusDays(1);
    while (d.getDayOfWeek() != java.time.DayOfWeek.MONDAY) d = d.plusDays(1);
    return d;
  }
}
