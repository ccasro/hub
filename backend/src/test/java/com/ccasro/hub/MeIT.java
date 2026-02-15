package com.ccasro.hub;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ccasro.hub.modules.iam.domain.ports.UserProfileRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeIT extends BaseIT {

  @Autowired UserProfileRepositoryPort users;

  @Test
  void me_without_token_returns_401() throws Exception {
    mvc.perform(get("/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void me_with_valid_jwt_returns_200_and_provisions_user() throws Exception {
    assertThat(users.findByAuth0Sub("auth0|user")).isEmpty();

    mvc.perform(get("/me").header("Authorization", bearer(userToken()))).andExpect(status().isOk());

    assertThat(users.findByAuth0Sub("auth0|user")).isPresent();
  }
}
