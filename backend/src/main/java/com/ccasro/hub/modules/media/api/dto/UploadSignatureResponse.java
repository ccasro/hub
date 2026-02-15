package com.ccasro.hub.modules.media.api.dto;

public record UploadSignatureResponse(
    String provider,
    String cloudName,
    String apiKey,
    long timestamp,
    String folder,
    String publicId,
    boolean overwrite,
    String signature) {}
