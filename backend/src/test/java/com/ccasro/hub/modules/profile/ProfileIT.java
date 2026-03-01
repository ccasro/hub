package com.ccasro.hub.modules.profile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ccasro.hub.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class ProfileIT extends BaseIT {

  @Test
  void get_me_sin_token_devuelve_401() throws Exception {
    mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void get_me_provisiona_usuario_y_devuelve_200() throws Exception {
    mvc.perform(get("/api/me").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("PLAYER"));
  }

  @Test
  void update_me_actualiza_perfil() throws Exception {
    givenPlayer();

    mvc.perform(
            put("/api/me")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                  "displayName": "Test Player Updated",
                  "description": "Jugador de padel",
                  "city": "Barcelona",
                  "countryCode": "ES",
                  "preferredSport": "PADEL",
                  "skillLevel": "INTERMEDIATE"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("Test Player Updated"))
        .andExpect(jsonPath("$.city").value("Barcelona"))
        .andExpect(jsonPath("$.onboardingCompleted").value(true));
  }

  @Test
  void update_me_sin_displayName_devuelve_400() throws Exception {
    givenPlayer();

    mvc.perform(
            put("/api/me")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"city": "Barcelona"}
                """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void request_owner_sin_token_devuelve_401() throws Exception {
    mvc.perform(post("/api/me/request-owner")).andExpect(status().isUnauthorized());
  }

  @Test
  void request_owner_devuelve_202_y_status_pending() throws Exception {
    givenPlayer();

    mvc.perform(post("/api/me/request-owner").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isAccepted());

    mvc.perform(get("/api/me").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ownerRequestStatus").value("PENDING"));
  }

  @Test
  void request_owner_dos_veces_devuelve_error() throws Exception {
    givenPlayer();

    mvc.perform(post("/api/me/request-owner").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isAccepted());

    mvc.perform(post("/api/me/request-owner").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isConflict());
  }
}
