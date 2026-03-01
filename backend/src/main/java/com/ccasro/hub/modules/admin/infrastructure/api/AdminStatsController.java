package com.ccasro.hub.modules.admin.infrastructure.api;

import com.ccasro.hub.modules.admin.infrastructure.api.dto.AdminStatsResponse;
import com.ccasro.hub.modules.admin.infrastructure.api.dto.AdminStatsResponseMapper;
import com.ccasro.hub.modules.admin.usecases.GetAdminStatsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Stats", description = "General system statistics")
@PreAuthorize("@authz.isAdmin()")
public class AdminStatsController {

  private final GetAdminStatsService getAdminStatsService;

  @GetMapping("/stats")
  public ResponseEntity<AdminStatsResponse> getStats() {
    var stats = getAdminStatsService.execute();
    return ResponseEntity.ok(AdminStatsResponseMapper.from(stats));
  }
}
