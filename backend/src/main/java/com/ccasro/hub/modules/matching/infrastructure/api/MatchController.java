package com.ccasro.hub.modules.matching.infrastructure.api;

import com.ccasro.hub.modules.matching.application.dto.CreateMatchRequestCommand;
import com.ccasro.hub.modules.matching.application.dto.MatchSlotResult;
import com.ccasro.hub.modules.matching.application.dto.SearchMatchSlotsQuery;
import com.ccasro.hub.modules.matching.domain.MatchFormat;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.MatchSkillLevel;
import com.ccasro.hub.modules.matching.domain.valueobjects.GeoPoint;
import com.ccasro.hub.modules.matching.domain.valueobjects.InvitationToken;
import com.ccasro.hub.modules.matching.infrastructure.api.dto.CreateMatchRequestRequest;
import com.ccasro.hub.modules.matching.infrastructure.api.dto.JoinMatchRequestRequest;
import com.ccasro.hub.modules.matching.infrastructure.api.dto.MatchRequestResponse;
import com.ccasro.hub.modules.matching.usecases.CreateMatchRequestService;
import com.ccasro.hub.modules.matching.usecases.GetMatchRequestService;
import com.ccasro.hub.modules.matching.usecases.JoinMatchRequestService;
import com.ccasro.hub.modules.matching.usecases.SearchMatchSlotsService;
import com.ccasro.hub.modules.resource.domain.valueobjects.ResourceId;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
@Tag(name = "Match", description = "Intelligent Match Making")
public class MatchController {

  private final SearchMatchSlotsService searchMatchSlotsService;
  private final CreateMatchRequestService createMatchRequestService;
  private final JoinMatchRequestService joinMatchRequestService;
  private final GetMatchRequestService getMatchRequestService;
  private final CurrentUserProvider currentUser;

  @GetMapping("/search")
  @Operation(tags = "Player - Match", summary = "Search available slots for a match in area")
  public ResponseEntity<List<MatchSlotResult>> search(
      @RequestParam double lat,
      @RequestParam double lng,
      @RequestParam(defaultValue = "10") double radiusKm,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTimeFrom,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTimeTo,
      @RequestParam(defaultValue = "90") int slotDurationMinutes,
      @RequestParam(defaultValue = "TWO_VS_TWO") MatchFormat format,
      @RequestParam(defaultValue = "ANY") MatchSkillLevel skillLevel) {

    return ResponseEntity.ok(
        searchMatchSlotsService.execute(
            new SearchMatchSlotsQuery(
                new GeoPoint(lat, lng),
                radiusKm,
                date,
                startTimeFrom,
                startTimeTo,
                slotDurationMinutes,
                format,
                skillLevel)));
  }

  @PostMapping("/requests")
  @Operation(tags = "Player - Match", summary = "Create a match request and block the slot")
  public ResponseEntity<MatchRequestResponse> create(
      @RequestBody @Valid CreateMatchRequestRequest request) {

    UserId organizerId = currentUser.getUserId();

    CreateMatchRequestCommand cmd =
        new CreateMatchRequestCommand(
            organizerId,
            ResourceId.of(request.resourceId()),
            request.bookingDate(),
            request.startTime(),
            request.slotDurationMinutes(),
            request.format(),
            request.skillLevel(),
            request.customMessage(),
            new GeoPoint(request.searchLat(), request.searchLng()),
            request.searchRadiusKm());

    MatchRequest matchRequest = createMatchRequestService.execute(cmd);

    return ResponseEntity.status(HttpStatus.CREATED).body(MatchRequestResponse.from(matchRequest));
  }

  @PostMapping("/join/{token}")
  @Operation(tags = "Player - Match", summary = "Join a match request by invitation token")
  public ResponseEntity<MatchRequestResponse> join(
      @PathVariable String token, @RequestBody @Valid JoinMatchRequestRequest request) {

    MatchRequest matchRequest =
        joinMatchRequestService.execute(InvitationToken.of(UUID.fromString(token)), request.team());

    return ResponseEntity.ok(MatchRequestResponse.from(matchRequest));
  }

  @GetMapping("/requests/{id}")
  @Operation(tags = "Player - Match", summary = "Get match request by id")
  public ResponseEntity<MatchRequestResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(MatchRequestResponse.from(getMatchRequestService.findById(id)));
  }

  @GetMapping("/join/{token}")
  @Operation(tags = "Player - Match", summary = "Get match request by invitation token")
  public ResponseEntity<MatchRequestResponse> getByToken(@PathVariable String token) {
    return ResponseEntity.ok(MatchRequestResponse.from(getMatchRequestService.findByToken(token)));
  }
}
