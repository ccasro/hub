package com.ccasro.hub.modules.resource.domain.exception;

public class ResourceImageNotFoundException extends RuntimeException {
  public ResourceImageNotFoundException() {
    super("Resource image not found");
  }
}
