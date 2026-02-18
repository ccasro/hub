package com.ccasro.hub.modules.catalog.application.query;

import com.ccasro.hub.modules.catalog.application.query.dto.PublicVenueDetailDto;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicVenueSummaryDto;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import java.util.List;
import java.util.Optional;

public interface PublicVenueQueryPort {
  List<PublicVenueSummaryDto> findAllPublicSummaries();

  Optional<PublicVenueDetailDto> findPublicDetailById(VenueId id);
}
