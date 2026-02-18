package com.ccasro.hub.modules.catalog.api.dto;

import com.ccasro.hub.modules.catalog.application.query.dto.ImageDto;

public record ImageResponse(String id, String url, boolean primary, int position) {
  public static ImageResponse from(ImageDto dto) {
    return new ImageResponse(dto.id(), dto.url(), dto.primary(), dto.position());
  }
}
