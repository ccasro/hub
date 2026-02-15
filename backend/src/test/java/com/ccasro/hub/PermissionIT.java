package com.ccasro.hub;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Import(PermissionIT.TestOnlyController.class)
class PermissionIT extends BaseIT {

  @Test
  void protected_endpoint_without_permission_returns_403() throws Exception {
    mvc.perform(get("/test/admin").header("Authorization", bearer(userToken())))
        .andExpect(status().isForbidden());
  }

  @Test
  void protected_endpoint_with_permission_returns_200() throws Exception {
    mvc.perform(get("/test/admin").header("Authorization", bearer(adminToken())))
        .andExpect(status().isOk());
  }

  @RestController
  static class TestOnlyController {
    @GetMapping("/test/admin")
    @PreAuthorize("hasAuthority('PERM_ADMIN')")
    public ResponseEntity<String> admin() {
      return ResponseEntity.ok("ok");
    }
  }
}
