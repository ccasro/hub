package com.ccasro.hub.modules.venue.application.dto;

import com.ccasro.hub.modules.venue.domain.Venue;

public record VenueWithCount(Venue venue, int resourceCount) {}
