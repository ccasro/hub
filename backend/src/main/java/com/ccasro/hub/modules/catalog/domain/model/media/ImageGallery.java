package com.ccasro.hub.modules.catalog.domain.model.media;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class ImageGallery {

  private final int maxImages;
  private final List<Image> images = new ArrayList<>();

  public ImageGallery(int maxImages, List<Image> initial) {
    if (maxImages <= 0) throw new IllegalArgumentException("maxImages must be > 0");
    this.maxImages = maxImages;
    if (initial != null) this.images.addAll(initial);
    normalizeInvariant();
  }

  public List<Image> images() {
    return List.copyOf(images);
  }

  public Image primaryOrNull() {
    return images.stream().filter(Image::primary).findFirst().orElse(null);
  }

  public void add(String publicIdRaw, String urlRaw) {
    if (images.size() >= maxImages) throw new IllegalStateException("max images reached");

    var publicId = new MediaPublicId(publicIdRaw);
    var url = MediaUrl.of(urlRaw);

    boolean exists = images.stream().anyMatch(i -> i.publicId().value().equals(publicId.value()));
    if (exists) throw new IllegalArgumentException("image already exists");

    int pos = images.size();
    boolean primary = images.isEmpty();

    images.add(Image.create(publicId, url, pos, primary));
    normalizeInvariant();
  }

  public void removeById(String imageIdRaw) {
    UUID imageId = UUID.fromString(imageIdRaw);

    int idx = indexOf(imageId);
    if (idx == -1) throw new IllegalArgumentException("image not found");

    images.remove(idx);
    normalizeInvariant();
  }

  public void setPrimaryById(String imageIdRaw) {
    UUID imageId = UUID.fromString(imageIdRaw);

    boolean found = false;
    for (var img : images) {
      boolean primary = img.id().value().equals(imageId);
      img.setPrimary(primary);
      if (primary) found = true;
    }
    if (!found) throw new IllegalArgumentException("image not found");

    normalizeInvariant();
  }

  private int indexOf(UUID imageId) {
    for (int i = 0; i < images.size(); i++) {
      if (images.get(i).id().value().equals(imageId)) return i;
    }
    return -1;
  }

  private void reindexPositions() {
    images.sort(Comparator.comparingInt(Image::position));
    for (int i = 0; i < images.size(); i++) images.get(i).setPosition(i);
  }

  private void normalizeInvariant() {
    if (images.size() > maxImages) throw new IllegalStateException("too many images loaded");

    reindexPositions();

    if (images.isEmpty()) return;

    var primaries = images.stream().filter(Image::primary).toList();
    if (primaries.isEmpty()) {
      images.get(0).setPrimary(true);
    } else if (primaries.size() > 1) {
      boolean first = true;
      for (var img : images) {
        if (img.primary()) {
          if (first) first = false;
          else img.setPrimary(false);
        }
      }
    }
  }
}
