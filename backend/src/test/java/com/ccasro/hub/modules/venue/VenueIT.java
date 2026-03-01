package com.ccasro.hub.modules.venue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ccasro.hub.BaseIT;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class VenueIT extends BaseIT {

  @Test
  void listar_venues_publicos_sin_token_devuelve_200() throws Exception {
    mvc.perform(get("/api/venues").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void crear_venue_sin_token_devuelve_401() throws Exception {
    mvc.perform(post("/api/owner/venues").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void crear_venue_como_player_devuelve_403() throws Exception {
    givenPlayer();

    mvc.perform(
            post("/api/owner/venues")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                  "name": "Mi Club", "street": "Calle 1",
                  "city": "Barcelona", "country": "España",
                  "postalCode": "08001", "latitude": 41.3851, "longitude": 2.1734
                }
                """))
        .andExpect(status().isForbidden());
  }

  @Test
  void crear_venue_como_owner_devuelve_200_status_pending() throws Exception {
    givenOwner();

    mvc.perform(
            post("/api/owner/venues")
                .header("Authorization", bearer(OWNER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                  "name": "Club del Owner", "street": "Carrer Test 1",
                  "city": "Barcelona", "country": "España",
                  "postalCode": "08001", "latitude": 41.3851, "longitude": 2.1734
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Club del Owner"))
        .andExpect(jsonPath("$.status").value("PENDING_REVIEW"));
  }

  @Test
  void aprobar_venue_como_player_devuelve_403() throws Exception {
    givenPlayer();

    mvc.perform(
            patch("/api/admin/venues/00000000-0000-0000-0000-000000000001/approve")
                .header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isForbidden());
  }

  @Test
  void flujo_completo_crear_y_aprobar_venue() throws Exception {
    givenOwner();
    givenAdmin();

    String response =
        mvc.perform(
                post("/api/owner/venues")
                    .header("Authorization", bearer(OWNER_TOKEN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                {
                  "name": "Club Aprovació", "street": "Carrer 1",
                  "city": "Barcelona", "country": "España",
                  "postalCode": "08001", "latitude": 41.3851, "longitude": 2.1734
                }
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("PENDING_REVIEW"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    String venueId = JsonPath.read(response, "$.id");

    mvc.perform(
            patch("/api/admin/venues/" + venueId + "/approve")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void flujo_reject_venue() throws Exception {
    givenOwner();
    givenAdmin();

    String response =
        mvc.perform(
                post("/api/owner/venues")
                    .header("Authorization", bearer(OWNER_TOKEN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                {
                  "name": "Club Rebutjat", "street": "Carrer 2",
                  "city": "Madrid", "country": "España",
                  "postalCode": "28001", "latitude": 40.4168, "longitude": -3.7038
                }
                """))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String venueId = JsonPath.read(response, "$.id");

    mvc.perform(
            patch("/api/admin/venues/" + venueId + "/reject")
                .header("Authorization", bearer(ADMIN_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"reason": "Documentació incompleta"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REJECTED"));
  }

  @Test
  void owner_pot_veure_els_seus_venues() throws Exception {
    givenOwner();

    mvc.perform(get("/api/owner/venues").header("Authorization", bearer(OWNER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void admin_veu_venues_pending() throws Exception {
    givenAdmin();

    mvc.perform(get("/api/admin/venues/pending").header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }
}
