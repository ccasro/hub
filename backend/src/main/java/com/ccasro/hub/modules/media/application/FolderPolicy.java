package com.ccasro.hub.modules.media.application;

import com.ccasro.hub.common.domain.media.MediaKey;
import com.ccasro.hub.modules.media.domain.UploadContext;
import com.ccasro.hub.modules.media.domain.UploadPurpose;
import org.springframework.stereotype.Component;

@Component
public class FolderPolicy {

  public String resolveFolder(UploadPurpose purpose, UploadContext ctx) {
    if (purpose == null) throw new IllegalArgumentException("purpose is required");
    if (ctx == null) throw new IllegalArgumentException("context is required");

    return switch (purpose) {
      case AVATAR -> MediaKey.avatarFolder(require("principalId", ctx.principalId()));

      case VENUE_IMAGE -> "users/"
          + require("principalId", ctx.principalId())
          + "/venues/"
          + require("venueId", ctx.venueId());
      case RESOURCE_IMAGE -> "users/"
          + require("principalId", ctx.principalId())
          + "/resources/"
          + require("resourceId", ctx.resourceId());
    };
  }

  private String require(String name, String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Missing " + name + " for folder resolution");
    }
    return value;
  }
}
