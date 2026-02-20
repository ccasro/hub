package com.ccasro.hub.modules.resource.domain;

import java.time.Instant;
import java.util.UUID;

public record ResourceImageSnapshot(
    UUID id, String url, String publicId, int displayOrder, Instant createdAt) {}
