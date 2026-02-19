package com.ccasro.hub.modules.iam.domain.exception;

public class Auth0CommunicationException extends RuntimeException {
  public Auth0CommunicationException(String message) {
    super(message);
  }
}
