package com.ccasro.hub.modules.catalog.domain.model.media;

public record ImagePosition(int value) {
  public ImagePosition {
    if (value < 0) throw new IllegalArgumentException("position must be >= 0");
  }
}
