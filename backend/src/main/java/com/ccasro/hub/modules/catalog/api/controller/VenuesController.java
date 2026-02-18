package com.ccasro.hub.modules.catalog.api.controller;

import com.ccasro.hub.modules.catalog.api.dto.venue.PublicVenueResponse;
import com.ccasro.hub.modules.catalog.api.dto.venue.VenueDetailResponse;
import com.ccasro.hub.modules.catalog.application.usecase.resource.ResourceListingUseCase;
import com.ccasro.hub.modules.catalog.application.usecase.venue.GetVenueByIdUseCase;
import com.ccasro.hub.modules.catalog.application.usecase.venue.VenueListingUseCase;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/venues")
@RequiredArgsConstructor
public class VenuesController {

  private final VenueListingUseCase venueListing;
  private final GetVenueByIdUseCase getVenue;
  private final ResourceListingUseCase resourceListing;

  @GetMapping
  public List<PublicVenueResponse> listPublicVenues() {
    return venueListing.listPublic().stream().map(PublicVenueResponse::from).toList();
  }

  @GetMapping("/{id}")
  public VenueDetailResponse get(@PathVariable String id) {
    var venue = getVenue.get(VenueId.of(id));
    return VenueDetailResponse.from(venue);
  }
}
