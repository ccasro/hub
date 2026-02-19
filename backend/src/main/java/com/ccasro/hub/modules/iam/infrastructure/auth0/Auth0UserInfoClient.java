package com.ccasro.hub.modules.iam.infrastructure.auth0;

import com.ccasro.hub.modules.iam.domain.exception.Auth0CommunicationException;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@ConditionalOnProperty(name = "auth0.userinfo.enabled", havingValue = "true", matchIfMissing = true)
public class Auth0UserInfoClient {

  private final RestClient restClient;
  private final URI userInfoUri;

  public Auth0UserInfoClient(
      RestClient.Builder builder, @Value("${auth0.issuer}") String issuerUri) {
    if (issuerUri == null || issuerUri.isBlank()) {
      throw new IllegalStateException("Missing property auth0.issuer-uri");
    }

    this.restClient = builder.build();

    String base = issuerUri.endsWith("/") ? issuerUri : issuerUri + "/";
    this.userInfoUri = URI.create(base + "userinfo");
  }

  public Auth0UserInfo getUserInfo(String accessToken) {
    try {
      return restClient
          .get()
          .uri(userInfoUri)
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
          .retrieve()
          .body(Auth0UserInfo.class);
    } catch (RestClientResponseException ex) {
      throw new Auth0CommunicationException("Error calling Auth0 /userinfo: ");
    } catch (RestClientException ex) {
      throw new Auth0CommunicationException("Error calling Auth0 /userinfo");
    }
  }
}
