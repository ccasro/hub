package com.ccasro.hub.modules.iam.domain.exception;

public class UserProfileNotFoundException extends RuntimeException {
  public UserProfileNotFoundException() {
    super("UserProfile not found");
  }
}
