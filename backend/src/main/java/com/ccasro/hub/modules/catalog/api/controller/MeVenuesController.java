package com.ccasro.hub.modules.catalog.api.controller;

import com.ccasro.hub.modules.catalog.api.dto.resource.CreateResourceRequest;
import com.ccasro.hub.modules.catalog.api.dto.resource.ResourceResponse;
import com.ccasro.hub.modules.catalog.api.dto.venue.*;
import com.ccasro.hub.modules.catalog.application.command.CreateResourceCommand;
import com.ccasro.hub.modules.catalog.application.command.CreateVenueCommand;
import com.ccasro.hub.modules.catalog.application.usecase.resource.CreateResourceUseCase;
import com.ccasro.hub.modules.catalog.application.usecase.venue.*;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.venue.Venue;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/me/venues")
@RequiredArgsConstructor
public class MeVenuesController {

  private final CreateVenueUseCase createVenue;
  private final CreateResourceUseCase createResource;
  private final PatchVenueDetailsUseCase patchVenue;
  private final ActivateVenueUseCase activateVenue;
  private final SuspendVenueUseCase suspendVenue;
  private final VenueListingUseCase venueListing;

  @PostMapping
  public ResponseEntity<VenueResponse> createVenue(@Valid @RequestBody CreateVenueRequest req) {

    Venue venue = createVenue.create(new CreateVenueCommand(req.name(), req.description()));

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(venue.id().toString())
            .toUri();

    return ResponseEntity.created(location).body(VenueResponse.from(venue));
  }

  @PostMapping("/{venueId}/resources")
  public ResponseEntity<ResourceResponse> createResource(
      @PathVariable String venueId, @Valid @RequestBody CreateResourceRequest req) {

    Resource resource =
        createResource.create(
            VenueId.of(venueId),
            new CreateResourceCommand(
                req.name(), req.description(), req.basePriceAmount(), req.basePriceCurrency()));

    URI location =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/resources/{id}")
            .buildAndExpand(resource.id().toString())
            .toUri();

    return ResponseEntity.created(location).body(ResourceResponse.from(resource));
  }

  @PutMapping("/{venueId}/activate")
  public ResponseEntity<Void> activate(@PathVariable String venueId) {
    activateVenue.activate(VenueId.of(venueId));
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{venueId}/suspend")
  public ResponseEntity<Void> suspend(@PathVariable String venueId) {
    suspendVenue.suspend(VenueId.of(venueId));
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{venueId}")
  public ResponseEntity<VenueDetailResponse> patchVenue(
      @PathVariable String venueId, @Valid @RequestBody PatchVenueRequest req) {
    var updated = patchVenue.patch(VenueId.of(venueId), req.toCommand());

    return ResponseEntity.ok(VenueDetailResponse.from(updated));
  }

  @GetMapping
  public List<MyVenueResponse> listMine() {
    return venueListing.listMine().stream().map(MyVenueResponse::from).toList();
  }
}
