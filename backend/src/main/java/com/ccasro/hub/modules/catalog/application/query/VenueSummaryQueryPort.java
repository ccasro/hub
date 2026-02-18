package com.ccasro.hub.modules.catalog.application.query;

import com.ccasro.hub.modules.catalog.application.query.dto.VenueSummaryDto;
import com.ccasro.hub.modules.iam.domain.UserId;
import java.util.List;

public interface VenueSummaryQueryPort {
  List<VenueSummaryDto> findSummariesByOwnerUserId(UserId ownerUserId);
}
