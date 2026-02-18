package com.ccasro.hub.modules.catalog.api.controller;

import com.ccasro.hub.modules.catalog.api.dto.resource.ResourceDetailResponse;
import com.ccasro.hub.modules.catalog.api.dto.resource.ResourceSummaryResponse;
import com.ccasro.hub.modules.catalog.application.usecase.resource.GetResourceByIdUseCase;
import com.ccasro.hub.modules.catalog.application.usecase.resource.ResourceListingUseCase;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourcesController {

  private final GetResourceByIdUseCase getResource;
  private final ResourceListingUseCase resourceListing;

  @GetMapping
  public List<ResourceSummaryResponse> listPublicResources(@RequestParam Optional<UUID> venueId) {
    Optional<VenueId> optionalVenueId = venueId.map(VenueId::new);

    return resourceListing.listPublic(optionalVenueId).stream()
        .map(ResourceSummaryResponse::from)
        .toList();
  }

  @GetMapping("/{id}")
  public ResourceDetailResponse get(@PathVariable String id) {
    Resource r = getResource.get(ResourceId.of(id));
    return ResourceDetailResponse.from(r);
  }
}
