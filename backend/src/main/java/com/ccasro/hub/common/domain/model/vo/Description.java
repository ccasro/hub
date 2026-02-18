package com.ccasro.hub.common.domain.model.vo;

import com.ccasro.hub.common.domain.model.TextConstraints;

public record Description(String value) {

  public static final int MAX_LEN = 1000;

  public Description {
    value = TextConstraints.optionalMax(value, "description", MAX_LEN);
  }

  public boolean isPresent() {
    return value != null;
  }
}
