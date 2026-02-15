package com.ccasro.hub.modules.media.infrastructure.cloudinary;

import com.ccasro.hub.modules.media.application.MediaStoragePort;
import com.cloudinary.Cloudinary;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CloudinaryMediaStorage implements MediaStoragePort {

  private final Cloudinary cloudinary;
  private final CloudinaryProps props;

  public CloudinaryMediaStorage(Cloudinary cloudinary, CloudinaryProps props) {
    this.cloudinary = cloudinary;
    this.props = props;
  }

  @Override
  public SignedUploadParams createSignedUploadParams(
      long timestampSeconds, String folder, String publicId, boolean overwrite) {

    if (folder == null || folder.isBlank()) {
      throw new IllegalArgumentException("folder is required");
    }

    if (publicId == null || publicId.isBlank()) {
      throw new IllegalArgumentException("publicId is required");
    }

    Map<String, Object> paramsToSign = new HashMap<>();
    paramsToSign.put("timestamp", timestampSeconds);
    paramsToSign.put("folder", folder);
    paramsToSign.put("public_id", publicId);

    if (overwrite) {
      paramsToSign.put("overwrite", true);
    }

    String signature = cloudinary.apiSignRequest(paramsToSign, props.apiSecret(), 2);

    return new SignedUploadParams(
        "cloudinary",
        props.cloudName(),
        props.apiKey(),
        timestampSeconds,
        folder,
        publicId,
        overwrite,
        signature);
  }
}
