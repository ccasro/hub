package com.ccasro.hub.modules.catalog.application.usecase.resource;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.modules.catalog.application.query.PublicResourceQueryPort;
import com.ccasro.hub.modules.catalog.application.query.ResourceSummaryQueryPort;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicResourceSummaryDto;
import com.ccasro.hub.modules.catalog.application.query.dto.ResourceSummaryDto;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.iam.domain.UserId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResourceListingUseCase {

  private final PublicResourceQueryPort publicResources;
  private final ResourceSummaryQueryPort myResources;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public List<PublicResourceSummaryDto> listPublic(Optional<VenueId> venueId) {
    return venueId
        .map(publicResources::findPublicSummariesByVenueId)
        .orElseGet(publicResources::findAllPublic);
  }

  @Transactional(readOnly = true)
  public List<ResourceSummaryDto> listMine(Optional<VenueId> venueId) {
    UserId ownerId = currentUser.getUserId();
    return venueId
        .map(vId -> myResources.findSummariesByOwnerUserIdAndVenueId(ownerId, vId))
        .orElseGet(() -> myResources.findSummariesByOwnerUserId(ownerId));
  }
}
