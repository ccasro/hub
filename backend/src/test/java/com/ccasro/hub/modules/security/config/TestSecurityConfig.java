package com.ccasro.hub.modules.security.config;

import java.time.Instant;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
public class TestSecurityConfig {

  @Bean
  JwtDecoder jwtDecoder() {
    return token -> {
      String sub =
          switch (token) {
            case "admin-token" -> "auth0|admin";
            case "owner-token" -> "auth0|owner";
            case "player2-token" -> "auth0|player2";
            default -> "auth0|user";
          };

      List<String> permissions =
          switch (token) {
            case "admin-token" -> List.of("ADMIN");
            case "owner-token" -> List.of("OWNER");
            case "player2-token", "player-token" -> List.of("PLAYER");
            default -> List.of();
          };

      return Jwt.withTokenValue(token)
          .header("alg", "none")
          .claim("sub", sub)
          .claim("permissions", List.of())
          .claim("scope", "")
          .issuedAt(Instant.now())
          .expiresAt(Instant.now().plusSeconds(300))
          .build();
    };
  }
}
