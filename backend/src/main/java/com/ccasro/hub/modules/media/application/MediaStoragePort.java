package com.ccasro.hub.modules.media.application;

public interface MediaStoragePort {

  SignedUploadParams createSignedUploadParams(
      long timestampSeconds, String folder, String publicId, boolean overwrite);

  record SignedUploadParams(
      String provider,
      String cloudName,
      String apiKey,
      long timestamp,
      String folder,
      String publicId,
      boolean overwrite,
      String signature) {}
}
