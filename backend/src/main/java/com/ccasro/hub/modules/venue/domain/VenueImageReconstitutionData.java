package com.ccasro.hub.modules.venue.domain;

import java.time.Instant;
import java.util.UUID;

public record VenueImageReconstitutionData(
    UUID id, String url, String publicId, int displayOrder, Instant createdAt) {}
