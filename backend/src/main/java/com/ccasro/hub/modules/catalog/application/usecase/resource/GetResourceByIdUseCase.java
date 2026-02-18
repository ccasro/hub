package com.ccasro.hub.modules.catalog.application.usecase.resource;

import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.port.ResourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetResourceByIdUseCase {

  private final ResourceRepositoryPort resources;

  @Transactional(readOnly = true)
  public Resource get(ResourceId id) {
    return resources
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Resource not found: " + id));
  }
}
