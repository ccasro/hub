package com.ccasro.hub.modules.venue.infrastructure.api;

import com.ccasro.hub.modules.venue.application.dto.CreateVenueCommand;
import com.ccasro.hub.modules.venue.application.dto.UpdateVenueCommand;
import com.ccasro.hub.modules.venue.domain.Venue;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.modules.venue.infrastructure.api.dto.AddVenueImageRequest;
import com.ccasro.hub.modules.venue.infrastructure.api.dto.CreateVenueRequest;
import com.ccasro.hub.modules.venue.infrastructure.api.dto.VenueResponse;
import com.ccasro.hub.modules.venue.usecases.*;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Venues", description = "Venue management")
public class VenueController {

  private final CreateVenueService createVenueService;
  private final UpdateVenueService updateVenueService;
  private final GetVenueService getVenueService;
  private final SuspendVenueService suspendVenueService;
  private final ReactivateVenueService reactivateVenueService;
  private final AddVenueImageService addVenueImageService;
  private final RemoveVenueImageService removeVenueImageService;
  private final CurrentUserProvider currentUser;

  // ── Public ───────────────────────────────────────────────────

  @GetMapping("/api/venues")
  @Operation(tags = "Public - Venues", summary = "List active venues")
  public ResponseEntity<List<VenueResponse>> listActive() {
    return ResponseEntity.ok(
        getVenueService.findAllActiveWithResourceCount().stream()
            .map(vc -> VenueResponse.from(vc.venue(), vc.resourceCount()))
            .toList());
  }

  @GetMapping("/api/venues/{id}")
  @Operation(tags = "Public - Venues", summary = "Venue detail")
  public ResponseEntity<VenueResponse> getById(@PathVariable UUID id) {
    Venue venue = getVenueService.findPublicById(VenueId.of(id));
    return ResponseEntity.ok(VenueResponse.from(venue));
  }

  @GetMapping("/api/venues/nearby")
  @Operation(tags = "Public - Venues", summary = "Active venues near a location")
  public ResponseEntity<List<VenueResponse>> nearby(
      @RequestParam double lat,
      @RequestParam double lng,
      @RequestParam(defaultValue = "5000") double radiusMeters) {
    return ResponseEntity.ok(
        getVenueService.findNearby(lat, lng, radiusMeters).stream()
            .map(VenueResponse::from)
            .toList());
  }

  // ── Owner ────────────────────────────────────────────────────

  @GetMapping("/api/owner/venues")
  @Operation(tags = "Owner - Venues", summary = "My venues")
  public ResponseEntity<List<VenueResponse>> myVenues() {
    return ResponseEntity.ok(
        getVenueService.findMyVenues(currentUser.getUserId()).stream()
            .map(VenueResponse::from)
            .toList());
  }

  @PostMapping("/api/owner/venues")
  @Operation(tags = "Owner - Venues", summary = "Create venue")
  public ResponseEntity<VenueResponse> create(@Valid @RequestBody CreateVenueRequest request) {
    CreateVenueCommand cmd =
        new CreateVenueCommand(
            request.name(), request.description(),
            request.street(), request.city(),
            request.country(), request.postalCode(),
            request.latitude(), request.longitude());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(VenueResponse.from(createVenueService.execute(cmd)));
  }

  @PutMapping("/api/owner/venues/{id}")
  @Operation(tags = "Owner - Venues", summary = "Update venue")
  public ResponseEntity<VenueResponse> update(
      @PathVariable UUID id, @Valid @RequestBody CreateVenueRequest request) {
    UpdateVenueCommand cmd =
        new UpdateVenueCommand(
            VenueId.of(id),
            request.name(),
            request.description(),
            request.street(),
            request.city(),
            request.country(),
            request.postalCode(),
            request.latitude(),
            request.longitude());
    return ResponseEntity.ok(VenueResponse.from(updateVenueService.execute(cmd)));
  }

  @PatchMapping("/api/owner/venues/{id}/suspend")
  @Operation(tags = "Owner - Venues", summary = "Suspend venue")
  public ResponseEntity<Void> suspend(@PathVariable UUID id) {
    suspendVenueService.execute(VenueId.of(id));
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/api/owner/venues/{id}/reactivate")
  @Operation(tags = "Owner - Venues", summary = "Reactivate venue")
  public ResponseEntity<Void> reactivate(@PathVariable UUID id) {
    reactivateVenueService.execute(VenueId.of(id));
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/api/owner/venues/{id}/images")
  @Operation(tags = "Owner - Media", summary = "Add image to venue")
  public ResponseEntity<VenueResponse> addImage(
      @PathVariable UUID id, @Valid @RequestBody AddVenueImageRequest request) {
    return ResponseEntity.ok(
        VenueResponse.from(
            addVenueImageService.execute(
                VenueId.of(id), new ImageUrl(request.url(), request.publicId()))));
  }

  @DeleteMapping("/api/owner/venues/{id}/images/{imageId}")
  @Operation(tags = "Owner - Media", summary = "Delete image from venue")
  public ResponseEntity<Void> removeImage(@PathVariable UUID id, @PathVariable UUID imageId) {
    removeVenueImageService.execute(VenueId.of(id), imageId);
    return ResponseEntity.noContent().build();
  }
}
