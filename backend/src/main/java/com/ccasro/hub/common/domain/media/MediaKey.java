package com.ccasro.hub.common.domain.media;

public final class MediaKey {
  private MediaKey() {}

  public static String subjectKey(String sub) {
    if (sub == null || sub.isBlank()) {
      throw new IllegalArgumentException("sub is required");
    }
    return sub.replace("|", "_");
  }

  public static String avatarFolder(String sub) {
    return "avatars/" + subjectKey(sub);
  }

  public static String avatarPrefix(String sub) {
    return avatarFolder(sub) + "/";
  }

  public static String avatarPublicId(String sub) {
    return avatarFolder(sub) + "/avatar";
  }

  public static String venueImagesFolder(String principalId, String venueId) {
    return "users/" + principalId + "/venues/" + venueId;
  }

  public static String resourceImagesFolder(String principalId, String resourceId) {
    return "users/" + principalId + "/resources/" + resourceId;
  }

  public static String ensureTrailingSlash(String folder) {
    if (folder.endsWith("/")) return folder;
    return folder + "/";
  }
}
