package com.ccasro.hub.modules.booking.infrastructure.email;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class BrevoEmailSender {

  @Value("${brevo.api.key}")
  private String apiKey;

  @Value("${app.mail.from:noreply@sportshub.com}")
  private String from;

  private RestClient restClient;

  @PostConstruct
  public void init() {
    this.restClient = RestClient.builder().baseUrl("https://api.brevo.com/v3").build();
  }

  public void send(String to, String subject, String htmlContent) {
    Map<String, Object> body =
        Map.of(
            "sender",
            Map.of("email", from),
            "to",
            List.of(Map.of("email", to)),
            "subject",
            subject,
            "htmlContent",
            htmlContent);

    restClient
        .post()
        .uri("/smtp/email")
        .header("api-key", apiKey)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve()
        .toBodilessEntity();

    log.info("Email sent via Brevo API to {}", to);
  }
}
