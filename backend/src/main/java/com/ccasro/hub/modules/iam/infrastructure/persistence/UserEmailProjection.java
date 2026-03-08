package com.ccasro.hub.modules.iam.infrastructure.persistence;

import java.util.UUID;

public interface UserEmailProjection {
  UUID getId();

  String getEmail();
}
