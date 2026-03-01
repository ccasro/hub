package com.ccasro.hub;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ccasro.hub.shared.application.ports.CurrentUserContextProvider;
import com.ccasro.hub.shared.domain.security.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Import(PermissionIT.TestOnlyController.class)
class PermissionIT extends BaseIT {

  @MockitoBean CurrentUserContextProvider current;

  @Test
  void protected_endpoint_without_permission_returns_403() throws Exception {
    when(current.role()).thenReturn(UserRole.PLAYER);

    mvc.perform(get("/test/admin").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isForbidden());
  }

  @Test
  void protected_endpoint_with_permission_returns_200() throws Exception {
    when(current.role()).thenReturn(UserRole.ADMIN);

    mvc.perform(get("/test/admin").header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk());
  }

  @Test
  void protected_endpoint_with_owner_permission_returns_200() throws Exception {
    when(current.role()).thenReturn(UserRole.OWNER);

    mvc.perform(get("/test/owner").header("Authorization", bearer(OWNER_TOKEN)))
        .andExpect(status().isOk());
  }

  @RestController
  static class TestOnlyController {
    @GetMapping("/test/admin")
    @PreAuthorize("@authz.isAdmin()")
    public ResponseEntity<String> admin() {
      return ResponseEntity.ok("ok");
    }

    @GetMapping("/test/owner")
    @PreAuthorize("@authz.isOwner()")
    public ResponseEntity<String> owner() {
      return ResponseEntity.ok("ok");
    }
  }
}
