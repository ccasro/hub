package com.ccasro.hub.modules.resource.usecases;

import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.exception.ResourceNotFoundException;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceStatus;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminResourceService {

  private final ResourceRepositoryPort resourceRepository;
  private final Clock clock;

  @Transactional(readOnly = true)
  @PreAuthorize("@authz.isAdmin()")
  public List<Resource> findPending() {
    return resourceRepository.findByStatus(ResourceStatus.PENDING_REVIEW);
  }

  @Transactional(readOnly = true)
  @PreAuthorize("@authz.isAdmin()")
  public List<Resource> findAll(int page, int size) {
    return resourceRepository.findAll(page, size);
  }

  @Transactional
  @PreAuthorize("@authz.isAdmin()")
  public Resource approve(ResourceId id) {
    Resource resource = findOrThrow(id);
    resource.approve(clock);
    return resourceRepository.save(resource);
  }

  @Transactional
  @PreAuthorize("@authz.isAdmin()")
  public Resource reject(ResourceId id, String reason) {
    Resource resource = findOrThrow(id);
    resource.reject(reason, clock);
    return resourceRepository.save(resource);
  }

  @Transactional
  @PreAuthorize("@authz.isAdmin()")
  public Resource adminSuspend(ResourceId id, String reason) {
    Resource resource = findOrThrow(id);
    resource.adminSuspend(reason, clock);
    return resourceRepository.save(resource);
  }

  @PreAuthorize("@authz.isAdmin()")
  private Resource findOrThrow(ResourceId id) {
    return resourceRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
  }
}
