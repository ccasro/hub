package com.ccasro.hub.modules.media.domain;

public record UploadContext(String principalId, String venueId, String resourceId) {
  public static UploadContext forAvatar(String principalId) {
    return new UploadContext(principalId, null, null);
  }

  public static UploadContext forVenueImage(String principalId, String venueId) {
    return new UploadContext(principalId, venueId, null);
  }

  public static UploadContext forResourceImage(String principalId, String resourceId) {
    return new UploadContext(principalId, null, resourceId);
  }
}
