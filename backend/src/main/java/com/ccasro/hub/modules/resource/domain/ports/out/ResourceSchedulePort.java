package com.ccasro.hub.modules.resource.domain.ports.out;

import com.ccasro.hub.modules.resource.domain.Resource;
import com.ccasro.hub.modules.resource.domain.valueobjects.DayOfWeek;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import java.time.LocalTime;

public interface ResourceSchedulePort {

  Resource removeSchedule(ResourceId resourceId, DayOfWeek day);

  Resource upsertSchedule(
      ResourceId resourceId, DayOfWeek day, LocalTime opening, LocalTime closing);
}
