package com.ccasro.hub.modules.catalog.api.controller;

import com.ccasro.hub.modules.catalog.api.dto.AddImageRequest;
import com.ccasro.hub.modules.catalog.api.dto.resource.ResourceDetailResponse;
import com.ccasro.hub.modules.catalog.application.usecase.resource.image.AddResourceImageUseCase;
import com.ccasro.hub.modules.catalog.application.usecase.resource.image.DeleteResourceImageUseCase;
import com.ccasro.hub.modules.catalog.application.usecase.resource.image.SetPrimaryResourceImageUseCase;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/resources")
@RequiredArgsConstructor
public class MeResourcesImagesController {
  private final AddResourceImageUseCase addImage;
  private final SetPrimaryResourceImageUseCase setPrimary;
  private final DeleteResourceImageUseCase deleteImage;

  @PostMapping("/{resourceId}/images")
  public ResponseEntity<ResourceDetailResponse> add(
      @PathVariable String resourceId, @Valid @RequestBody AddImageRequest req) {
    var updated = addImage.add(ResourceId.of(resourceId), req.publicId(), req.url());
    return ResponseEntity.ok(ResourceDetailResponse.from(updated));
  }

  @PutMapping("/{resourceId}/images/{imageId}/primary")
  public ResponseEntity<ResourceDetailResponse> setPrimary(
      @PathVariable String resourceId, @PathVariable String imageId) {
    var updated = setPrimary.setPrimary(ResourceId.of(resourceId), imageId);
    return ResponseEntity.ok(ResourceDetailResponse.from(updated));
  }

  @DeleteMapping("/{resourceId}/images/{imageId}")
  public ResponseEntity<ResourceDetailResponse> delete(
      @PathVariable String resourceId, @PathVariable String imageId) {
    var updated = deleteImage.delete(ResourceId.of(resourceId), imageId);
    return ResponseEntity.ok(ResourceDetailResponse.from(updated));
  }
}
