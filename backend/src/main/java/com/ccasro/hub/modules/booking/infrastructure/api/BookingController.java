package com.ccasro.hub.modules.booking.infrastructure.api;

import com.ccasro.hub.modules.booking.application.dto.CreateBookingResult;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.BookingCommandMapper;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.BookingResponse;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.BookingResponseMapper;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.CancelBookingRequest;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.CreateBookingRequest;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.CreateBookingResponse;
import com.ccasro.hub.modules.booking.usecases.*;
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
    return ResponseEntity.ok(BookingResponseMapper.fromMyBookings(getMyBookingsService.execute()));
  }

  @PostMapping("/api/bookings")
  @Operation(tags = "Player - Bookings", summary = "Create booking")
  public ResponseEntity<CreateBookingResponse> create(
      @Valid @RequestBody CreateBookingRequest request) {
    CreateBookingResult result =
        createBookingService.execute(BookingCommandMapper.toCreateCommand(request));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CreateBookingResponse(
                BookingResponseMapper.from(result.booking()), result.clientSecret()));
  }

  @PatchMapping("/api/bookings/{id}/cancel")
  @Operation(tags = "Player - Bookings", summary = "Cancel my booking")
  public ResponseEntity<BookingResponse> cancel(
      @PathVariable UUID id, @RequestBody CancelBookingRequest request) {
    return ResponseEntity.ok(
        BookingResponseMapper.from(
            cancelBookingService.execute(BookingCommandMapper.toCancelCommand(id, request))));
  }

  // ── Owner ────────────────────────────────────────────────────

  @GetMapping("/api/owner/bookings")
  @Operation(tags = "Owner - Bookings", summary = "All bookings of my venues")
  public ResponseEntity<List<BookingResponse>> myVenueBookings(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        BookingResponseMapper.fromVenueBookings(getOwnerBookingsService.execute(page, size)));
  }

  @GetMapping("/api/owner/venues/{venueId}/bookings")
  @Operation(tags = "Owner - Bookings", summary = "Bookings of a specific venue")
  public ResponseEntity<List<BookingResponse>> venueBookings(
      @PathVariable UUID venueId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        BookingResponseMapper.fromVenueBookings(
            getVenueBookingsService.execute(venueId, page, size)));
  }
}
