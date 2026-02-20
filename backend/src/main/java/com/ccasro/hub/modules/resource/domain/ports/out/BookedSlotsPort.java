package com.ccasro.hub.modules.resource.domain.ports.out;

import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.modules.resource.domain.valueobjects.SlotRange;
import java.time.LocalDate;
import java.util.List;

public interface BookedSlotsPort {
  List<SlotRange> findBookedSlots(ResourceId resourceId, LocalDate date);
}
