package com.ccasro.hub.shared.domain.valueobjects;

import java.net.URI;

public record ImageUrl(String url, String publicId) {

  private static final String CLOUDINARY_HOST = "res.cloudinary.com";

  public ImageUrl {

    if (url == null || url.isBlank()) {
      throw new IllegalArgumentException("ImageUrl must not be null or blank");
    }

    if (publicId == null || publicId.isBlank()) {
      throw new IllegalArgumentException("PublicId must not be null or blank");
    }

    url = url.trim();
    publicId = publicId.trim();

    URI uri = URI.create(url);

    if (!"https".equals(uri.getScheme()) || !CLOUDINARY_HOST.equals(uri.getHost())) {
      throw new IllegalArgumentException("ImageUrl must be a valid Cloudinary HTTPS URL");
    }
  }
}
