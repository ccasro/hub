package com.ccasro.hub.modules.media.application.mapper;

import com.ccasro.hub.modules.media.api.dto.UploadSignatureRequest;
import com.ccasro.hub.modules.media.domain.UploadContext;
import org.springframework.stereotype.Component;

@Component
public class UploadContextMapper {

  public UploadContext toContext(String principalId, UploadSignatureRequest req) {
    return switch (req.purpose()) {
      case AVATAR -> UploadContext.forAvatar(principalId);

      case VENUE_IMAGE -> UploadContext.forVenueImage(principalId, req.venueId());

      case RESOURCE_IMAGE -> UploadContext.forResourceImage(principalId, req.resourceId());
    };
  }
}
