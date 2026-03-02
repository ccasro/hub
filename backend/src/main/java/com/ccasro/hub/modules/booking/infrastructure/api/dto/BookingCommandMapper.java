package com.ccasro.hub.modules.booking.infrastructure.api.dto;

import com.ccasro.hub.modules.booking.application.dto.CancelBookingCommand;
import com.ccasro.hub.modules.booking.application.dto.CreateBookingCommand;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import java.util.UUID;

public final class BookingCommandMapper {

  private BookingCommandMapper() {}

  public static CreateBookingCommand toCreateCommand(CreateBookingRequest request) {
    return new CreateBookingCommand(
        ResourceId.of(request.resourceId()), request.bookingDate(), request.startTime());
  }

  public static CancelBookingCommand toCancelCommand(UUID bookingId, CancelBookingRequest request) {
    return new CancelBookingCommand(BookingId.of(bookingId), request.reason());
  }
}
