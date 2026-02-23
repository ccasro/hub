package com.ccasro.hub.modules.booking.infrastructure.api;

import com.ccasro.hub.modules.booking.application.dto.CancelBookingCommand;
import com.ccasro.hub.modules.booking.application.dto.CreateBookingCommand;
import com.ccasro.hub.modules.booking.application.dto.CreateBookingResult;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.BookingResponse;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.CancelBookingRequest;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.CreateBookingRequest;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.CreateBookingResponse;
import com.ccasro.hub.modules.booking.usecases.CancelBookingService;
import com.ccasro.hub.modules.booking.usecases.CreateBookingService;
import com.ccasro.hub.modules.booking.usecases.GetMyBookingsService;
import com.ccasro.hub.modules.booking.usecases.GetVenueBookingsService;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Bookings Management")
public class BookingController {

  private final CreateBookingService createBookingService;
  private final CancelBookingService cancelBookingService;
  private final GetMyBookingsService getMyBookingsService;
  private final GetVenueBookingsService getVenueBookingsService;

  @GetMapping("/api/bookings/my")
  public ResponseEntity<List<BookingResponse>> myBookings() {
    return ResponseEntity.ok(
        getMyBookingsService.execute().stream().map(BookingResponse::from).toList());
  }

  @PostMapping("/api/bookings")
  public ResponseEntity<CreateBookingResponse> create(
      @Valid @RequestBody CreateBookingRequest request) {
    CreateBookingResult result =
        createBookingService.execute(
            new CreateBookingCommand(
                ResourceId.of(request.resourceId()), request.bookingDate(), request.startTime()));

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CreateBookingResponse(
                BookingResponse.from(result.booking()), result.clientSecret()));
  }

  @PatchMapping("/api/bookings/{id}/cancel")
  public ResponseEntity<BookingResponse> cancel(
      @PathVariable UUID id, @RequestBody CancelBookingRequest request) {
    CancelBookingCommand cmd = new CancelBookingCommand(BookingId.of(id), request.reason());
    return ResponseEntity.ok(BookingResponse.from(cancelBookingService.execute(cmd)));
  }

  @GetMapping("/api/venues/{venueId}/bookings")
  public ResponseEntity<List<BookingResponse>> venueBookings(
      @PathVariable UUID venueId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        getVenueBookingsService.execute(venueId, page, size).stream()
            .map(BookingResponse::from)
            .toList());
  }
}
