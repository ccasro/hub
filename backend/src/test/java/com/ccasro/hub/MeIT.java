package com.ccasro.hub;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeIT extends BaseIT {

  @Autowired UserProfileRepositoryPort users;

  @Test
  void me_without_token_returns_401() throws Exception {
    mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void me_with_valid_jwt_returns_200_and_provisions_user() throws Exception {
    assertThat(users.findByAuth0Id(new Auth0Id("auth0|user"))).isEmpty();

    mvc.perform(get("/api/me").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk());

    assertThat(users.findByAuth0Id(new Auth0Id("auth0|user"))).isPresent();
  }
}
