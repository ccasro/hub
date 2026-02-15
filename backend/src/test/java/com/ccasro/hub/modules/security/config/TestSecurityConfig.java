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
      boolean admin = "admin-token".equals(token);

      String sub = admin ? "auth0|admin" : "auth0|user";
      List<String> permissions = admin ? List.of("ADMIN") : List.of();

      return Jwt.withTokenValue(token)
          .header("alg", "none")
          .claim("sub", sub)
          .claim("permissions", permissions)
          .claim("scope", "")
          .issuedAt(Instant.now())
          .expiresAt(Instant.now().plusSeconds(300))
          .build();
    };
  }
}
