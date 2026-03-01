package com.ccasro.hub.modules.admin;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ccasro.hub.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class AdminIT extends BaseIT {

  @Test
  void admin_stats_sin_token_devuelve_401() throws Exception {
    mvc.perform(get("/api/admin/stats")).andExpect(status().isUnauthorized());
  }

  @Test
  void admin_stats_como_player_devuelve_403() throws Exception {
    givenPlayer();

    mvc.perform(get("/api/admin/stats").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isForbidden());
  }

  @Test
  void admin_stats_como_admin_devuelve_200() throws Exception {
    givenAdmin();

    mvc.perform(get("/api/admin/stats").header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalUsers").exists())
        .andExpect(jsonPath("$.totalVenues").exists())
        .andExpect(jsonPath("$.totalBookings").exists());
  }

  @Test
  void listar_users_como_admin_devuelve_200() throws Exception {
    givenAdmin();

    mvc.perform(get("/api/admin/users").header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void listar_users_como_player_devuelve_403() throws Exception {
    givenPlayer();

    mvc.perform(get("/api/admin/users").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isForbidden());
  }

  @Test
  void toggle_active_user_como_admin() throws Exception {
    givenAdmin();
    var player = givenPlayer();

    mvc.perform(
            patch("/api/admin/users/" + player.getId().value() + "/toggle-active")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isNoContent());
  }

  @Test
  void cambiar_rol_user_a_owner() throws Exception {
    givenAdmin();
    var player = givenPlayer();

    mvc.perform(
            patch("/api/admin/users/" + player.getId().value() + "/role")
                .header("Authorization", bearer(ADMIN_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"role": "OWNER"}
                """))
        .andExpect(status().isNoContent());
  }

  @Test
  void pending_owners_devuelve_lista() throws Exception {
    givenAdmin();

    mvc.perform(get("/api/admin/users/pending-owners").header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void flujo_aprobar_owner_request() throws Exception {
    givenAdmin();
    var player = givenPlayer();

    mvc.perform(post("/api/me/request-owner").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isAccepted());

    mvc.perform(
            patch("/api/admin/users/" + player.getId().value() + "/approve-owner")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isNoContent());

    mvc.perform(
            get("/api/admin/users/" + player.getId().value())
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("OWNER"));
  }

  @Test
  void flujo_rechazar_owner_request() throws Exception {
    givenAdmin();
    var player = givenPlayer();

    mvc.perform(post("/api/me/request-owner").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isAccepted());

    mvc.perform(
            patch("/api/admin/users/" + player.getId().value() + "/reject-owner")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isNoContent());

    mvc.perform(
            get("/api/admin/users/" + player.getId().value())
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("PLAYER")); // rol no canvia
  }

  @Test
  void admin_puede_cancelar_cualquier_booking() throws Exception {
    givenAdmin();

    mvc.perform(
            patch("/api/admin/bookings/00000000-0000-0000-0000-000000000999/cancel")
                .header("Authorization", bearer(ADMIN_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"reason": "admin cancel test"}
                """))
        .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(200, 404));
  }
}
