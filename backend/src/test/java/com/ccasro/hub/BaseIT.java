package com.ccasro.hub;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfo;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfoClient;
import com.ccasro.hub.modules.security.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestcontainersConfiguration.class})
public abstract class BaseIT {

  @Autowired protected MockMvc mvc;

  @MockitoBean protected Auth0UserInfoClient auth0UserInfoClient;

  @BeforeEach
  void baseStubs() {
    when(auth0UserInfoClient.getUserInfo(anyString()))
        .thenAnswer(
            inv -> {
              String token = inv.getArgument(0, String.class);

              boolean admin = "admin-token".equals(token);
              String sub = admin ? "auth0|admin" : "auth0|user";
              String email = admin ? "admin@test.local" : "user@test.local";

              return new Auth0UserInfo(sub, email);
            });
  }

  protected static String bearer(String token) {
    return "Bearer " + token;
  }

  protected static String adminToken() {
    return "admin-token";
  }

  protected static String userToken() {
    return "user-token";
  }
}
