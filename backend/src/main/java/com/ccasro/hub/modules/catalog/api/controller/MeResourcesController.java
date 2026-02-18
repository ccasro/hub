package com.ccasro.hub.modules.catalog.api.controller;

import com.ccasro.hub.modules.catalog.api.dto.resource.PatchResourceRequest;
import com.ccasro.hub.modules.catalog.api.dto.resource.ResourceDetailResponse;
import com.ccasro.hub.modules.catalog.application.usecase.resource.ActivateResourceUseCase;
import com.ccasro.hub.modules.catalog.application.usecase.resource.PatchResourceDetailsUseCase;
import com.ccasro.hub.modules.catalog.application.usecase.resource.SuspendResourceUseCase;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/resources")
@RequiredArgsConstructor
public class MeResourcesController {

  private final PatchResourceDetailsUseCase patchResource;
  private final ActivateResourceUseCase activateResource;
  private final SuspendResourceUseCase suspendResource;

  @PatchMapping("/{resourceId}")
  public ResponseEntity<ResourceDetailResponse> patch(
      @PathVariable String resourceId, @Valid @RequestBody PatchResourceRequest req) {
    var updated = patchResource.patch(ResourceId.of(resourceId), req.toCommand());
    return ResponseEntity.ok(ResourceDetailResponse.from(updated));
  }

  @PutMapping("/{resourceId}/activate")
  public ResponseEntity<Void> activate(@PathVariable String resourceId) {
    activateResource.activate(ResourceId.of(resourceId));
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{resourceId}/suspend")
  public ResponseEntity<Void> suspend(@PathVariable String resourceId) {
    suspendResource.suspend(ResourceId.of(resourceId));
    return ResponseEntity.noContent().build();
  }
}
