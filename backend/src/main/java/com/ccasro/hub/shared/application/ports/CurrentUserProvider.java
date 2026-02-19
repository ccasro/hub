package com.ccasro.hub.shared.application.ports;

import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.util.Set;

public interface CurrentUserProvider {
  String getSub();

  UserId getUserId();

  Set<String> authorities();
}
