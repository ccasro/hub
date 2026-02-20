package com.ccasro.hub.modules.resource.infrastructure.api.dto;

import com.ccasro.hub.modules.resource.domain.Resource;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ResourceResponse(
    UUID id,
    UUID venueId,
    String name,
    String description,
    String type,
    int slotDurationMinutes,
    String status,
    String rejectReason,
    List<DayScheduleResponse> schedules,
    List<PriceRuleResponse> priceRules,
    List<ResourceImageResponse> images,
    Instant createdAt,
    Instant updatedAt) {
  public static ResourceResponse from(Resource r) {
    return new ResourceResponse(
        r.getId().value(),
        r.getVenueId().value(),
        r.getName().value(),
        r.getDescription(),
        r.getType().name(),
        r.getSlotDuration().minutes(),
        r.getStatus().name(),
        r.getRejectReason(),
        r.getSchedules().values().stream().map(DayScheduleResponse::from).toList(),
        r.getPriceRules().stream().map(PriceRuleResponse::from).toList(),
        r.getImages().stream().map(ResourceImageResponse::from).toList(),
        r.getCreatedAt(),
        r.getUpdatedAt());
  }
}
