package com.ccasro.hub.modules.catalog.api.controller;

import com.ccasro.hub.modules.catalog.api.dto.AddImageRequest;
import com.ccasro.hub.modules.catalog.api.dto.venue.VenueDetailResponse;
import com.ccasro.hub.modules.catalog.application.usecase.venue.image.AddVenueImageUseCase;
import com.ccasro.hub.modules.catalog.application.usecase.venue.image.DeleteVenueImageUseCase;
import com.ccasro.hub.modules.catalog.application.usecase.venue.image.SetPrimaryVenueImageUseCase;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/venues")
@RequiredArgsConstructor
public class MeVenuesImagesController {

  private final AddVenueImageUseCase addImage;
  private final SetPrimaryVenueImageUseCase setPrimary;
  private final DeleteVenueImageUseCase deleteImage;

  @PostMapping("/{venueId}/images")
  public ResponseEntity<VenueDetailResponse> add(
      @PathVariable String venueId, @Valid @RequestBody AddImageRequest req) {
    var updated = addImage.add(VenueId.of(venueId), req.publicId(), req.url());
    return ResponseEntity.ok(VenueDetailResponse.from(updated));
  }

  @PutMapping("/{venueId}/images/{imageId}/primary")
  public ResponseEntity<VenueDetailResponse> setPrimary(
      @PathVariable String venueId, @PathVariable String imageId) {
    var updated = setPrimary.setPrimary(VenueId.of(venueId), imageId);
    return ResponseEntity.ok(VenueDetailResponse.from(updated));
  }

  @DeleteMapping("/{venueId}/images/{imageId}")
  public ResponseEntity<VenueDetailResponse> delete(
      @PathVariable String venueId, @PathVariable String imageId) {
    var updated = deleteImage.delete(VenueId.of(venueId), imageId);
    return ResponseEntity.ok(VenueDetailResponse.from(updated));
  }
}
