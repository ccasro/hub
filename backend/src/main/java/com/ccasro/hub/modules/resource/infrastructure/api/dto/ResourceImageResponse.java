package com.ccasro.hub.modules.resource.infrastructure.api.dto;

import com.ccasro.hub.modules.resource.domain.ResourceImageSnapshot;
import java.util.UUID;

public record ResourceImageResponse(UUID id, String url, int displayOrder) {
  public static ResourceImageResponse from(ResourceImageSnapshot img) {
    return new ResourceImageResponse(img.id(), img.url(), img.displayOrder());
  }
}
