package com.ccasro.hub.modules.catalog.application.query;

import com.ccasro.hub.modules.catalog.application.query.dto.PublicResourceSummaryDto;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import java.util.List;

public interface PublicResourceQueryPort {
  List<PublicResourceSummaryDto> findAllPublic();

  List<PublicResourceSummaryDto> findPublicSummariesByVenueId(VenueId venueId);
}
