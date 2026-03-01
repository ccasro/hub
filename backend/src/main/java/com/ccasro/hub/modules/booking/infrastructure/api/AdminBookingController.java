package com.ccasro.hub.modules.booking.infrastructure.api;

import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.BookingResponse;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.BookingResponseMapper;
import com.ccasro.hub.modules.booking.infrastructure.api.dto.CancelBookingRequest;
import com.ccasro.hub.modules.booking.usecases.AdminBookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
@Tag(name = "Admin - Bookings", description = "Admin Booking Management")
@PreAuthorize("@authz.isAdmin()")
public class AdminBookingController {

  private final AdminBookingService adminBookingService;

  @GetMapping
  public ResponseEntity<List<BookingResponse>> listAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        BookingResponseMapper.fromMyBookings(adminBookingService.findAll(page, size)));
  }

  @PatchMapping("/{id}/cancel")
  public ResponseEntity<BookingResponse> cancel(
      @PathVariable UUID id, @RequestBody CancelBookingRequest request) {
    return ResponseEntity.ok(
        BookingResponseMapper.from(adminBookingService.cancel(BookingId.of(id), request.reason())));
  }
}
