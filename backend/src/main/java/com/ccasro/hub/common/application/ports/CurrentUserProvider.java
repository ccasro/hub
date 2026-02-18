package com.ccasro.hub.common.application.ports;

import com.ccasro.hub.modules.iam.domain.UserId;
import java.util.Set;

public interface CurrentUserProvider {
  String getSub();

  UserId getUserId();

  Set<String> authorities();
}
