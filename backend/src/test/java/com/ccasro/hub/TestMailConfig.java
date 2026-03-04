package com.ccasro.hub;

import com.ccasro.hub.modules.booking.infrastructure.email.BrevoEmailSender;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestMailConfig {

  @Bean
  @Primary
  public BrevoEmailSender brevoEmailSender() {
    return Mockito.mock(BrevoEmailSender.class);
  }
}
