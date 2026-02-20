package com.ccasro.hub.modules.resource.application.dto;

import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceType;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;

public record CreateResourceCommand(
    VenueId venueId, String name, String description, ResourceType type, int slotDurationMinutes) {}
