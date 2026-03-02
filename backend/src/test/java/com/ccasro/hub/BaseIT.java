package com.ccasro.hub;

import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.modules.iam.domain.valueobjects.DisplayName;
import com.ccasro.hub.modules.iam.domain.valueobjects.Email;
import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfo;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfoClient;
import com.ccasro.hub.modules.security.config.TestSecurityConfig;
import com.ccasro.hub.shared.domain.security.UserRole;
import com.ccasro.hub.shared.domain.valueobjects.CountryCode;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestcontainersConfiguration.class, TestMailConfig.class})
@Transactional
public abstract class BaseIT {

  @Autowired protected MockMvc mvc;
  @Autowired protected UserProfileRepositoryPort users;

  @MockitoBean protected Auth0UserInfoClient auth0UserInfoClient;

  // tokens
  protected static final String PLAYER_TOKEN = "player-token";
  protected static final String PLAYER2_TOKEN = "player2-token";
  protected static final String OWNER_TOKEN = "owner-token";
  protected static final String ADMIN_TOKEN = "admin-token";

  @BeforeEach
  void baseStubs() {
    when(auth0UserInfoClient.getUserInfo(anyString()))
        .thenAnswer(
            inv -> {
              String token = inv.getArgument(0, String.class);
              return switch (token) {
                case ADMIN_TOKEN -> new Auth0UserInfo("auth0|admin", "admin@test.local");
                case OWNER_TOKEN -> new Auth0UserInfo("auth0|owner", "owner@test.local");
                case PLAYER2_TOKEN -> new Auth0UserInfo("auth0|player2", "player2@test.local");
                default -> new Auth0UserInfo("auth0|user", "user@test.local");
              };
            });
  }

  protected static String bearer(String token) {
    return "Bearer " + token;
  }

  protected UserProfile givenPlayer() {
    return givenUser("auth0|user", "user@test.local", UserRole.PLAYER, PLAYER_TOKEN);
  }

  protected UserProfile givenPlayer2() {
    return givenUser("auth0|player2", "player2@test.local", UserRole.PLAYER, PLAYER2_TOKEN);
  }

  protected UserProfile givenOwner() {
    return givenUser("auth0|owner", "owner@test.local", UserRole.OWNER, OWNER_TOKEN);
  }

  protected UserProfile givenAdmin() {
    return givenUser("auth0|admin", "admin@test.local", UserRole.ADMIN, ADMIN_TOKEN);
  }

  private UserProfile givenUser(String auth0Id, String email, UserRole role, String token) {
    return users
        .findByAuth0Id(new Auth0Id(auth0Id))
        .orElseGet(
            () -> {
              UserProfile p =
                  UserProfile.reconstitute(
                      UserId.newId(),
                      new Auth0Id(auth0Id),
                      new Email(email),
                      true,
                      new DisplayName("Test " + role.name()),
                      null,
                      null,
                      null,
                      role,
                      OwnerRequestStatus.NONE,
                      null,
                      null,
                      "Barcelona",
                      new CountryCode("ES"),
                      true,
                      true,
                      Instant.now(),
                      Instant.now(),
                      Instant.now());
              return users.save(p);
            });
  }
}
