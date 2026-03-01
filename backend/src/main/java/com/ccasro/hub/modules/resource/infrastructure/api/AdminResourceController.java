package com.ccasro.hub.modules.resource.infrastructure.api;

import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.infrastructure.api.dto.ResourceResponse;
import com.ccasro.hub.modules.resource.infrastructure.api.dto.ResourceResponseMapper;
import com.ccasro.hub.modules.resource.usecases.AdminResourceService;
import com.ccasro.hub.modules.venue.infrastructure.api.dto.RejectVenueRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/resources")
@RequiredArgsConstructor
@Tag(name = "Admin - Resources", description = "Admin Resources Management")
@PreAuthorize("@authz.isAdmin()")
public class AdminResourceController {

  private final AdminResourceService adminResourceService;

  @GetMapping
  @Operation(summary = "List resources")
  public ResponseEntity<List<ResourceResponse>> listAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        ResourceResponseMapper.fromList(adminResourceService.findAll(page, size)));
  }

  @GetMapping("/pending")
  @Operation(summary = "List pending resources")
  public ResponseEntity<List<ResourceResponse>> pending() {
    return ResponseEntity.ok(ResourceResponseMapper.fromList(adminResourceService.findPending()));
  }

  @PatchMapping("/{id}/approve")
  @Operation(summary = "Approve resource")
  public ResponseEntity<ResourceResponse> approve(@PathVariable UUID id) {
    return ResponseEntity.ok(
        ResourceResponseMapper.from(adminResourceService.approve(ResourceId.of(id))));
  }

  @PatchMapping("/{id}/reject")
  @Operation(summary = "Reject resource")
  public ResponseEntity<ResourceResponse> reject(
      @PathVariable UUID id, @Valid @RequestBody RejectVenueRequest request) {
    return ResponseEntity.ok(
        ResourceResponseMapper.from(
            adminResourceService.reject(ResourceId.of(id), request.reason())));
  }

  @PatchMapping("/{id}/suspend")
  @Operation(summary = "Suspend resource")
  public ResponseEntity<ResourceResponse> suspend(
      @PathVariable UUID id, @Valid @RequestBody RejectVenueRequest request) {
    return ResponseEntity.ok(
        ResourceResponseMapper.from(
            adminResourceService.adminSuspend(ResourceId.of(id), request.reason())));
  }
}
