package com.ccasro.hub.modules.catalog.application.usecase.venue;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.modules.catalog.application.query.PublicVenueQueryPort;
import com.ccasro.hub.modules.catalog.application.query.VenueSummaryQueryPort;
import com.ccasro.hub.modules.catalog.application.query.dto.PublicVenueSummaryDto;
import com.ccasro.hub.modules.catalog.application.query.dto.VenueSummaryDto;
import com.ccasro.hub.modules.iam.domain.UserId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VenueListingUseCase {

  private final PublicVenueQueryPort publicVenues;
  private final VenueSummaryQueryPort myVenues;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public List<PublicVenueSummaryDto> listPublic() {
    return publicVenues.findAllPublicSummaries();
  }

  @Transactional(readOnly = true)
  public List<VenueSummaryDto> listMine() {
    UserId ownerId = currentUser.getUserId();
    return myVenues.findSummariesByOwnerUserId(ownerId);
  }
}
