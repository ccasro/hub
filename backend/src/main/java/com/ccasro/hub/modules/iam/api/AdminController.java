package com.ccasro.hub.modules.iam.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
  @GetMapping("/admin/ping")
  @PreAuthorize("hasAuthority('PERM_users:read')")
  public String ping() {
    return "ok";
  }

  @PostMapping("/admin/ping")
  @PreAuthorize("hasAuthority('PERM_users:write')")
  public String pingWrite() {
    return "ok";
  }
}
