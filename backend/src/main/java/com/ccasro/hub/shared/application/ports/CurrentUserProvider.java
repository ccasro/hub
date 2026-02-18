package com.ccasro.hub.shared.application.ports;

import java.util.Set;

public interface CurrentUserProvider {
  String getSub();

  Set<String> authorities();
}
