package com.ccasro.hub.modules.catalog.application.query;

import com.ccasro.hub.modules.catalog.application.query.dto.ResourceSummaryDto;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.iam.domain.UserId;
import java.util.List;

public interface ResourceSummaryQueryPort {
  List<ResourceSummaryDto> findSummariesByOwnerUserId(UserId ownerId);

  List<ResourceSummaryDto> findSummariesByOwnerUserIdAndVenueId(UserId ownerId, VenueId venueId);
}
