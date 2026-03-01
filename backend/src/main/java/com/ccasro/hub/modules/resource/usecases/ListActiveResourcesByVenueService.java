package com.ccasro.hub.modules.resource.usecases;

import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.ports.out.ResourceRepositoryPort;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListActiveResourcesByVenueService {

  private final ResourceRepositoryPort resourceRepository;

  @Transactional(readOnly = true)
  public List<Resource> execute(VenueId venueId) {
    return resourceRepository.findActiveByVenueId(venueId);
  }
}
