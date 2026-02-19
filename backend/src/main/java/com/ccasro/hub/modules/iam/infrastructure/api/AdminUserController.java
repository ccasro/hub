package com.ccasro.hub.modules.iam.infrastructure.api;

import com.ccasro.hub.modules.iam.infrastructure.api.dto.AdminUserProfileResponse;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.ChangeRoleRequest;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.UserProfileResponse;
import com.ccasro.hub.modules.iam.usecases.AdminUserService;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("@authz.isAdmin()")
public class AdminUserController {

  private final AdminUserService adminUserService;

  @GetMapping
  public ResponseEntity<List<AdminUserProfileResponse>> listAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(adminUserService.findAll(page, size));
  }

  @GetMapping("/pending-owners")
  public ResponseEntity<List<UserProfileResponse>> pendingOwners() {
    return ResponseEntity.ok(adminUserService.findPendingOwners());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserProfileResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(adminUserService.findById(new UserId(id)));
  }

  @PatchMapping("/{id}/approve-owner")
  public ResponseEntity<Void> approveOwner(@PathVariable UUID id) {
    adminUserService.approveOwnerRequest(new UserId(id));
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/reject-owner")
  public ResponseEntity<Void> rejectOwner(@PathVariable UUID id) {
    adminUserService.rejectOwnerRequest(new UserId(id));
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/role")
  public ResponseEntity<Void> changeRole(
      @PathVariable UUID id, @RequestBody ChangeRoleRequest request) {
    adminUserService.changeRole(new UserId(id), request.role());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/toggle-active")
  public ResponseEntity<Void> toggleActive(@PathVariable UUID id) {
    adminUserService.toggleActive(new UserId(id));
    return ResponseEntity.noContent().build();
  }
}
