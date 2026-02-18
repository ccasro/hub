package com.ccasro.hub.common.domain.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ccasro.hub.shared.domain.MediaKey;
import org.junit.jupiter.api.Test;

class MediaKeyTest {

  @Test
  void subjectKey_replacesPipeWithUnderscore() {
    assertEquals("auth0_123", MediaKey.subjectKey("auth0|123"));
  }

  @Test
  void avatarFolder_usesSanitizedSubject() {
    assertEquals("avatars/auth0_123", MediaKey.avatarFolder("auth0|123"));
  }

  @Test
  void avatarPrefix_endsWithSlash() {
    assertEquals("avatars/auth0_123/", MediaKey.avatarPrefix("auth0|123"));
  }

  @Test
  void avatarPublicId_buildsFullPublicId() {
    assertEquals("avatars/auth0_123/avatar", MediaKey.avatarPublicId("auth0|123"));
  }

  @Test
  void subjectKey_blank_throws() {
    assertThrows(IllegalArgumentException.class, () -> MediaKey.subjectKey(" "));
  }
}
