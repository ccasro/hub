package com.ccasro.hub.modules.venue.domain;

import java.time.Instant;
import java.util.UUID;

public record VenueImageSnapshot(
    UUID id, String url, String publicId, int displayOrder, Instant createdAt) {}
