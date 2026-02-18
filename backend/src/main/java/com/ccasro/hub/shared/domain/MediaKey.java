package com.ccasro.hub.shared.domain;

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
}
