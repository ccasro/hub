package com.ccasro.hub.modules.catalog.application.query.dto;

import com.ccasro.hub.modules.catalog.domain.model.media.Image;

public record ImageDto(String id, String url, boolean primary, int position) {
  public static ImageDto from(Image img) {
    return new ImageDto(
        img.id().value().toString(), img.url().toString(), img.primary(), img.position());
  }
}
