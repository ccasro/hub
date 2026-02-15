package com.ccasro.hub.modules.media.domain;

public record UploadContext(String principalId, String companyId, String venueId, String courtId) {
  public static UploadContext forAvatar(String principalId) {
    return new UploadContext(principalId, null, null, null);
  }

  public static UploadContext forCompanyLogo(String principalId, String companyId) {
    return new UploadContext(principalId, companyId, null, null);
  }

  public static UploadContext forVenueImage(String principalId, String companyId, String venueId) {
    return new UploadContext(principalId, companyId, venueId, null);
  }

  public static UploadContext forCourtImage(String principalId, String companyId, String courtId) {
    return new UploadContext(principalId, companyId, null, courtId);
  }
}
