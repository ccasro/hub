package com.ccasro.hub.modules.resource.infrastructure.api;

import com.ccasro.hub.modules.resource.application.dto.AddPriceRuleCommand;
import com.ccasro.hub.modules.resource.application.dto.CreateResourceCommand;
import com.ccasro.hub.modules.resource.application.dto.SetScheduleCommand;
import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.exception.ResourceNotFoundException;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.infrastructure.api.dto.*;
import com.ccasro.hub.modules.resource.usecases.*;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Resources", description = "Venue Resources Management")
@Slf4j
public class ResourceController {

  private final CreateResourceService createResourceService;
  private final SetScheduleService setScheduleService;
  private final AddPriceRuleService addPriceRuleService;
  private final SuspendResourceService suspendResourceService;
  private final ReactivateResourceService reactivateResourceService;
  private final AddResourceImageService addResourceImageService;
  private final GetResourceAvailabilityService availabilityService;
  private final GetOwnerResourcesService getOwnerResourcesService;
  private final RemoveResourceImageService removeResourceImageService;
  private final ResourceRepositoryPort resourceRepository;

  // ── Public ───────────────────────────────────────────────────

  @GetMapping("/api/venues/{venueId}/resources")
  @Operation(tags = "Public - Resources", summary = "List active resources of a venue")
  public ResponseEntity<List<ResourceResponse>> listByVenue(@PathVariable UUID venueId) {
    return ResponseEntity.ok(
        resourceRepository.findActiveByVenueId(VenueId.of(venueId)).stream()
            .map(ResourceResponse::from)
            .toList());
  }

  @GetMapping("/api/resources/{id}/slots")
  @Operation(
      tags = "Public - Availability",
      summary = "Get slot availability for a resource on a given date")
  public ResponseEntity<List<SlotAvailabilityResponse>> getSlots(
      @PathVariable UUID id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return ResponseEntity.ok(
        availabilityService.execute(ResourceId.of(id), date).stream()
            .map(SlotAvailabilityResponse::from)
            .toList());
  }

  // ── Owner ────────────────────────────────────────────────────

  @GetMapping("/api/owner/resources")
  @Operation(tags = "Owner - Resources", summary = "Get all resources of my venues")
  public ResponseEntity<List<ResourceResponse>> getMyResources() {
    return ResponseEntity.ok(getOwnerResourcesService.execute());
  }

  @PostMapping("/api/owner/venues/{venueId}/resources")
  @Operation(tags = "Owner - Resources", summary = "Create a new resource in a venue")
  public ResponseEntity<ResourceResponse> create(
      @PathVariable UUID venueId, @Valid @RequestBody CreateResourceRequest request) {
    CreateResourceCommand cmd =
        new CreateResourceCommand(
            VenueId.of(venueId),
            request.name(),
            request.description(),
            request.type(),
            request.slotDurationMinutes());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResourceResponse.from(createResourceService.execute(cmd)));
  }

  @PutMapping("/api/owner/resources/{id}/schedules")
  @Operation(tags = "Owner - Resources", summary = "Set schedule for a specific day")
  public ResponseEntity<ResourceResponse> setSchedule(
      @PathVariable UUID id, @RequestBody SetScheduleRequest request) {

    log.info(
        "SetSchedule request: day={} opening={} closing={}",
        request.dayOfWeek(),
        request.openingTime(),
        request.closingTime());

    SetScheduleCommand cmd =
        new SetScheduleCommand(
            ResourceId.of(id), request.dayOfWeek(),
            request.openingTime(), request.closingTime());
    return ResponseEntity.ok(ResourceResponse.from(setScheduleService.execute(cmd)));
  }

  @PostMapping("/api/owner/resources/{id}/price-rules")
  @Operation(tags = "Owner - Pricing", summary = "Add a price rule to a resource")
  public ResponseEntity<ResourceResponse> addPriceRule(
      @PathVariable UUID id, @Valid @RequestBody AddPriceRuleRequest request) {
    AddPriceRuleCommand cmd =
        new AddPriceRuleCommand(
            ResourceId.of(id),
            request.dayType(),
            request.startTime(),
            request.endTime(),
            request.price(),
            request.currency());
    return ResponseEntity.ok(ResourceResponse.from(addPriceRuleService.execute(cmd)));
  }

  @DeleteMapping("/api/owner/resources/{id}/price-rules/{ruleId}")
  @Operation(tags = "Owner - Pricing", summary = "Remove a price rule from a resource")
  public ResponseEntity<Void> removePriceRule(@PathVariable UUID id, @PathVariable UUID ruleId) {
    Resource resource =
        resourceRepository.findById(ResourceId.of(id)).orElseThrow(ResourceNotFoundException::new);
    resource.removePriceRule(ruleId, Clock.systemUTC());
    resourceRepository.save(resource);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/api/owner/resources/{id}/suspend")
  @Operation(tags = "Owner - Resources", summary = "Suspend a resource")
  public ResponseEntity<Void> suspend(@PathVariable UUID id) {
    suspendResourceService.execute(ResourceId.of(id));
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/api/owner/resources/{id}/reactivate")
  @Operation(tags = "Owner - Resources", summary = "Reactivate a resource")
  public ResponseEntity<Void> reactivate(@PathVariable UUID id) {
    reactivateResourceService.execute(ResourceId.of(id));
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/api/owner/resources/{id}/images")
  @Operation(tags = "Owner - Media", summary = "Add an image to a resource")
  public ResponseEntity<ResourceResponse> addImage(
      @PathVariable UUID id, @Valid @RequestBody AddResourceImageRequest request) {
    return ResponseEntity.ok(
        ResourceResponse.from(
            addResourceImageService.execute(
                ResourceId.of(id), new ImageUrl(request.url(), request.publicId()))));
  }

  @DeleteMapping("/api/owner/resources/{id}/images/{imageId}")
  @Operation(tags = "Owner - Media", summary = "Delete image from resource")
  public ResponseEntity<Void> removeImage(@PathVariable UUID id, @PathVariable UUID imageId) {
    removeResourceImageService.execute(id, imageId);
    return ResponseEntity.ok().build();
  }
}
