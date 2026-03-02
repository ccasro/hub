package com.ccasro.hub.modules.resource.infrastructure.api.dto;

import com.ccasro.hub.modules.resource.domain.Resource;
import java.util.List;

public final class ResourceResponseMapper {

  private ResourceResponseMapper() {}

  public static ResourceResponse from(Resource resource) {
    return ResourceResponse.from(resource);
  }

  public static List<ResourceResponse> fromList(List<Resource> resources) {
    return resources.stream().map(ResourceResponse::from).toList();
  }
}
