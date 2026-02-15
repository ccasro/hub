package com.ccasro.hub.common.application.ports;

import java.util.Set;

public interface CurrentUserProvider {
  String getSub();

  Set<String> authorities();
}
