package com.ccasro.hub.modules.security.jwt;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

class AudienceValidatorTest {

  @Test
  void validate_ok_when_aud_contains_expected() {
    var v = new AudienceValidator("padelhub-api");

    Jwt jwt =
        new Jwt(
            "t",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of("alg", "none"),
            Map.of("sub", "auth0|123", "aud", List.of("padelhub-api")));

    OAuth2TokenValidatorResult r = v.validate(jwt);
    assertThat(r.hasErrors()).isFalse();
  }

  @Test
  void validate_fails_when_aud_missing_expected() {
    var v = new AudienceValidator("padelhub-api");

    Jwt jwt =
        new Jwt(
            "t",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of("alg", "none"),
            Map.of("sub", "auth0|123", "aud", List.of("other-api")));

    OAuth2TokenValidatorResult r = v.validate(jwt);
    assertThat(r.hasErrors()).isTrue();
    assertThat(r.getErrors()).isNotEmpty();
  }

  @Test
  void validate_fails_when_aud_is_null_or_absent() {
    var v = new AudienceValidator("padelhub-api");

    Jwt jwt =
        new Jwt(
            "t",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of("alg", "none"),
            Map.of("sub", "auth0|123"));

    OAuth2TokenValidatorResult r = v.validate(jwt);
    assertThat(r.hasErrors()).isTrue();
  }
}
