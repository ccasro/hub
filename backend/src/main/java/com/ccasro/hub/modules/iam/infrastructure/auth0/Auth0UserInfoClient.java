package com.ccasro.hub.modules.iam.infrastructure.auth0;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "auth0.userinfo.enabled", havingValue = "true", matchIfMissing = true)
public class Auth0UserInfoClient {

  private final RestClient rest = RestClient.create();
  private final String userInfoUrl;

  public Auth0UserInfoClient(@Value("${auth0.issuer}") String issuer) {
    if (issuer == null || issuer.isBlank()) {
      throw new IllegalStateException("Missing property auth0.issuer");
    }
    this.userInfoUrl = (issuer.endsWith("/") ? issuer : issuer + "/") + "userinfo";
  }

  public Auth0UserInfo fetch(String accessToken) {
    return rest.get()
        .uri(userInfoUrl)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .retrieve()
        .body(Auth0UserInfo.class);
  }
}
