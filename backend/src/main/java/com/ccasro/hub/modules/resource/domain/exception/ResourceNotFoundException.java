package com.ccasro.hub.modules.resource.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException() {
    super("Resource not found");
  }
}
