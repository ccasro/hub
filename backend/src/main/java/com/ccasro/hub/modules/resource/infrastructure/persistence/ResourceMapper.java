package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.DaySchedule;
import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.ResourceImageReconstitutionData;
import com.ccasro.hub.modules.resource.domain.ResourceImageSnapshot;
import com.ccasro.hub.modules.resource.domain.valueobjects.*;
import com.ccasro.hub.modules.venue.domain.valueobjects.VenueId;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

  public Resource toDomain(ResourceEntity e) {

    List<DaySchedule> schedules =
        e.getSchedules().stream()
            .sorted(Comparator.comparing(ResourceScheduleEntity::getDayOfWeek))
            .map(
                s ->
                    new DaySchedule(
                        s.getId(), s.getDayOfWeek(), s.getOpeningTime(), s.getClosingTime()))
            .toList();

    List<PriceRuleReconstitutionData> priceRules =
        e.getPriceRules().stream()
            .sorted(
                Comparator.comparing(ResourcePriceRuleEntity::getDayType)
                    .thenComparing(ResourcePriceRuleEntity::getStartTime)
                    .thenComparing(ResourcePriceRuleEntity::getEndTime))
            .map(
                p ->
                    new PriceRuleReconstitutionData(
                        p.getId(),
                        p.getDayType(),
                        p.getStartTime(),
                        p.getEndTime(),
                        p.getPrice(),
                        p.getCurrency()))
            .toList();

    List<ResourceImageReconstitutionData> images =
        e.getImages().stream()
            .sorted(Comparator.comparingInt(ResourceImageEntity::getDisplayOrder))
            .map(
                img ->
                    new ResourceImageReconstitutionData(
                        img.getId(),
                        img.getUrl(),
                        img.getPublicId(),
                        img.getDisplayOrder(),
                        img.getCreatedAt()))
            .toList();

    return Resource.reconstitute(
        ResourceId.of(e.getId()),
        VenueId.of(e.getVenueId()),
        new ResourceName(e.getName()),
        e.getDescription(),
        e.getType(),
        new SlotDuration(e.getSlotDuration()),
        schedules,
        priceRules,
        images,
        e.getStatus(),
        e.getRejectReason(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }

  public ResourceEntity toEntity(Resource d) {
    ResourceEntity e = new ResourceEntity();
    e.setId(d.getId().value());
    e.setVenueId(d.getVenueId().value());
    e.setCreatedAt(d.getCreatedAt());
    updateEntity(d, e);
    return e;
  }

  public void updateEntity(Resource d, ResourceEntity e) {
    e.setName(d.getName().value());
    e.setDescription(d.getDescription());
    e.setType(d.getType());
    e.setSlotDuration(d.getSlotDuration().minutes());
    e.setStatus(d.getStatus());
    e.setRejectReason(d.getRejectReason());
    e.setUpdatedAt(d.getUpdatedAt());

    e.getSchedules().clear();
    d.getSchedules().values().stream()
        .sorted(Comparator.comparing(DaySchedule::getDayOfWeek))
        .forEach(
            s -> {
              ResourceScheduleEntity se = new ResourceScheduleEntity();
              se.setId(s.getId());
              se.setResource(e);
              se.setDayOfWeek(s.getDayOfWeek());
              se.setOpeningTime(s.getOpeningTime());
              se.setClosingTime(s.getClosingTime());
              e.getSchedules().add(se);
            });

    e.getPriceRules().clear();
    d.getPriceRules().stream()
        .sorted(
            Comparator.comparing(PriceRule::getDayType)
                .thenComparing(PriceRule::getStartTime)
                .thenComparing(PriceRule::getEndTime))
        .forEach(
            p -> {
              ResourcePriceRuleEntity pe = new ResourcePriceRuleEntity();
              pe.setId(p.getId());
              pe.setResource(e);
              pe.setDayType(p.getDayType());
              pe.setStartTime(p.getStartTime());
              pe.setEndTime(p.getEndTime());
              pe.setPrice(p.getPrice());
              pe.setCurrency(p.getCurrency());
              e.getPriceRules().add(pe);
            });

    e.getImages().clear();
    d.getImages().stream()
        .sorted(Comparator.comparingInt(ResourceImageSnapshot::displayOrder))
        .forEach(
            img -> {
              ResourceImageEntity ie = new ResourceImageEntity();
              ie.setId(img.id());
              ie.setResource(e);
              ie.setUrl(img.url());
              ie.setPublicId(img.publicId());
              ie.setDisplayOrder(img.displayOrder());
              ie.setCreatedAt(img.createdAt());
              e.getImages().add(ie);
            });
  }
}
