package com.ccasro.hub.modules.booking.infrastructure.api;

import com.ccasro.hub.modules.booking.application.dto.CancelBookingCommand;
import com.ccasro.hub.modules.booking.application.dto.CreateBookingCommand;
import com.ccasro.hub.modules.booking.application.dto.CreateBookingResult;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.BookingResponse;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.CancelBookingRequest;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.CreateBookingRequest;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.CreateBookingResponse;
import com.ccasro.hub.modules.booking.usecases.*;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import io.swagger.v3.oas.annotations.Operation;
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
  private final GetOwnerBookingsService getOwnerBookingsService;

  // ── Player ───────────────────────────────────────────────────

  @GetMapping("/api/bookings/my")
  @Operation(tags = "Player - Bookings", summary = "My bookings")
  public ResponseEntity<List<BookingResponse>> myBookings() {
    return ResponseEntity.ok(
        getMyBookingsService.execute().stream()
            .map(view -> BookingResponse.from(view)) // ← lambda en vez de method reference
            .toList());
  }

  @PostMapping("/api/bookings")
  @Operation(tags = "Player - Bookings", summary = "Create booking")
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
  @Operation(tags = "Player - Bookings", summary = "Cancel my booking")
  public ResponseEntity<BookingResponse> cancel(
      @PathVariable UUID id, @RequestBody CancelBookingRequest request) {
    return ResponseEntity.ok(
        BookingResponse.from(
            cancelBookingService.execute(
                new CancelBookingCommand(BookingId.of(id), request.reason()))));
  }

  // ── Owner ────────────────────────────────────────────────────

  @GetMapping("/api/owner/bookings")
  @Operation(tags = "Owner - Bookings", summary = "All bookings of my venues")
  public ResponseEntity<List<BookingResponse>> myVenueBookings(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        getOwnerBookingsService.execute(page, size).stream().map(BookingResponse::from).toList());
  }

  @GetMapping("/api/owner/venues/{venueId}/bookings")
  @Operation(tags = "Owner - Bookings", summary = "Bookings of a specific venue")
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
