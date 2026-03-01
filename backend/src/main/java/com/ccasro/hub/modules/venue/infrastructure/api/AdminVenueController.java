package com.ccasro.hub.modules.venue.infrastructure.api;

import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.modules.venue.infrastructure.api.dto.RejectVenueRequest;
import com.ccasro.hub.modules.venue.infrastructure.api.dto.VenueResponse;
import com.ccasro.hub.modules.venue.infrastructure.api.dto.VenueResponseMapper;
import com.ccasro.hub.modules.venue.usecases.AdminVenueService;
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
@RequestMapping("/api/admin/venues")
@RequiredArgsConstructor
@Tag(name = "Admin - Venues", description = "Venue Admin Management")
@PreAuthorize("@authz.isAdmin()")
public class AdminVenueController {

  private final AdminVenueService adminVenueService;

  @GetMapping
  @Operation(summary = "List venues")
  public ResponseEntity<List<VenueResponse>> listAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(VenueResponseMapper.fromList(adminVenueService.findAll(page, size)));
  }

  @GetMapping("/pending")
  @Operation(summary = "Pending review venues")
  public ResponseEntity<List<VenueResponse>> pending() {
    return ResponseEntity.ok(VenueResponseMapper.fromList(adminVenueService.findPending()));
  }

  @PatchMapping("/{id}/approve")
  @Operation(summary = "Approve venue")
  public ResponseEntity<VenueResponse> approve(@PathVariable UUID id) {
    return ResponseEntity.ok(VenueResponseMapper.from(adminVenueService.approve(VenueId.of(id))));
  }

  @PatchMapping("/{id}/reject")
  @Operation(summary = "Reject venue")
  public ResponseEntity<VenueResponse> reject(
      @PathVariable UUID id, @Valid @RequestBody RejectVenueRequest request) {
    return ResponseEntity.ok(
        VenueResponseMapper.from(adminVenueService.reject(VenueId.of(id), request.reason())));
  }

  @PatchMapping("/{id}/suspend")
  @Operation(summary = "Suspend venue")
  public ResponseEntity<VenueResponse> suspend(
      @PathVariable UUID id, @Valid @RequestBody RejectVenueRequest request) {
    return ResponseEntity.ok(
        VenueResponseMapper.from(adminVenueService.adminSuspend(VenueId.of(id), request.reason())));
  }
}
