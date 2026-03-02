package com.ccasro.hub.modules.resource.infrastructure.persistence;

import com.ccasro.hub.modules.resource.domain.valueobjects.DayType;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResourcePriceRuleJpaRepository
    extends JpaRepository<ResourcePriceRuleEntity, UUID> {

  List<ResourcePriceRuleEntity> findByResourceId(UUID resourceId);

  Optional<ResourcePriceRuleEntity> findFirstByResourceId(UUID resourceId);

  @Query(
      """
        SELECT p FROM ResourcePriceRuleEntity p
        WHERE p.resource.id = :resourceId
        AND p.startTime <= :time
        AND p.endTime > :time
        AND p.dayType IN :dayTypes
        ORDER BY CASE p.dayType
            WHEN 'MON' THEN 1
            WHEN 'TUE' THEN 1
            WHEN 'WED' THEN 1
            WHEN 'THU' THEN 1
            WHEN 'FRI' THEN 1
            WHEN 'SAT' THEN 1
            WHEN 'SUN' THEN 1
            WHEN 'WEEKDAY' THEN 2
            WHEN 'WEEKEND' THEN 2
        END ASC
        """)
  List<ResourcePriceRuleEntity> findApplicableRulesByResourceId(
      @Param("resourceId") UUID resourceId,
      @Param("time") LocalTime time,
      @Param("dayTypes") List<DayType> dayTypes);

  @Query(
      """
    SELECT p FROM ResourcePriceRuleEntity p
    WHERE p.resource.id = :resourceId
    AND p.dayType IN :dayTypes
    ORDER BY CASE p.dayType
        WHEN 'MON' THEN 1
        WHEN 'TUE' THEN 1
        WHEN 'WED' THEN 1
        WHEN 'THU' THEN 1
        WHEN 'FRI' THEN 1
        WHEN 'SAT' THEN 1
        WHEN 'SUN' THEN 1
        WHEN 'WEEKDAY' THEN 2
        WHEN 'WEEKEND' THEN 2
    END ASC
    """)
  List<ResourcePriceRuleEntity> findApplicableRules(
      @Param("resourceId") UUID resourceId, @Param("dayTypes") List<DayType> dayTypes);
}
