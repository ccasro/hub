package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.valueobjects.DayOfWeek;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resource_schedule")
@Getter
@Setter
public class ResourceScheduleEntity {

  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resource_id", nullable = false)
  private ResourceEntity resource;

  @Enumerated(EnumType.STRING)
  @Column(name = "day_of_week", nullable = false, length = 10)
  private DayOfWeek dayOfWeek;

  @Column(name = "opening_time", nullable = false)
  private LocalTime openingTime;

  @Column(name = "closing_time", nullable = false)
  private LocalTime closingTime;
}
