package com.ccasro.hub.modules.media.application.usecase;

import com.ccasro.hub.modules.media.application.FolderPolicy;
import com.ccasro.hub.modules.media.application.mapper.UploadContextMapper;
import com.ccasro.hub.modules.media.application.ports.MediaStoragePort;
import com.ccasro.hub.modules.media.domain.UploadContext;
import com.ccasro.hub.modules.media.infrastructure.api.dto.UploadSignatureRequest;
import com.ccasro.hub.modules.media.infrastructure.api.dto.UploadSignatureResponse;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GenerateUploadSignatureUseCase {
  private final FolderPolicy folderPolicy;
  private final MediaStoragePort mediaStorage;
  private final UploadContextMapper mapper;

  public GenerateUploadSignatureUseCase(
      FolderPolicy folderPolicy, MediaStoragePort mediaStorage, UploadContextMapper mapper) {
    this.folderPolicy = folderPolicy;
    this.mediaStorage = mediaStorage;
    this.mapper = mapper;
  }

  public UploadSignatureResponse request(String principalId, UploadSignatureRequest req) {

    String sanitizedPrincipalId = sanitizePrincipalId(principalId);

    validate(sanitizedPrincipalId, req);

    UploadContext ctx = mapper.toContext(sanitizedPrincipalId, req);

    String folder = folderPolicy.resolveFolder(req.purpose(), ctx);

    long ts = System.currentTimeMillis() / 1000;

    String publicId =
        switch (req.purpose()) {
          case AVATAR -> "avatar";
          case VENUE_IMAGE, RESOURCE_IMAGE -> UUID.randomUUID().toString();
        };

    boolean overwrite =
        switch (req.purpose()) {
          case AVATAR -> true;
          default -> false;
        };

    MediaStoragePort.SignedUploadParams p =
        mediaStorage.createSignedUploadParams(ts, folder, publicId, overwrite);

    return new UploadSignatureResponse(
        p.provider(),
        p.cloudName(),
        p.apiKey(),
        p.timestamp(),
        p.folder(),
        p.publicId(),
        p.overwrite(),
        p.signature());
  }

  private void validate(String principalId, UploadSignatureRequest req) {
    if (principalId == null || principalId.isBlank()) {
      throw new IllegalArgumentException("principalId is required");
    }
    if (req == null || req.purpose() == null) {
      throw new IllegalArgumentException("purpose is required");
    }

    switch (req.purpose()) {
      case AVATAR -> {}
      case VENUE_IMAGE -> require(req.venueId(), "venueId is required for VENUE_IMAGE");
      case RESOURCE_IMAGE -> require(req.resourceId(), "resourceId is required for RESOURCE_IMAGE");
    }
  }

  private void require(String v, String msg) {
    if (v == null || v.isBlank()) throw new IllegalArgumentException(msg);
  }

  private String sanitizePrincipalId(String raw) {
    if (raw == null || raw.isBlank()) {
      throw new IllegalArgumentException("principalId is required");
    }
    return raw.replace('|', '_').replace(':', '_');
  }
}
