package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.valueobjects.DayType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resource_price_rule")
@Getter
@Setter
public class ResourcePriceRuleEntity {

  @Id
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resource_id", nullable = false)
  private ResourceEntity resource;

  @Enumerated(EnumType.STRING)
  @Column(name = "day_type", nullable = false, length = 20)
  private DayType dayType;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Column(nullable = false, precision = 8, scale = 2)
  private BigDecimal price;

  @Column(nullable = false, length = 3)
  private String currency;
}
