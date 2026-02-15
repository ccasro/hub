package com.ccasro.hub;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
class BackendApplicationTests extends BaseIT {

  @Test
  void contextLoads() {}
}
