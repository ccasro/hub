package com.ccasro.hub.modules.iam.infrastructure.api;

import com.ccasro.hub.modules.iam.infrastructure.api.dto.UpdateAvatarRequest;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.UpdateMeRequest;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.UserProfileResponse;
import com.ccasro.hub.modules.iam.usecases.*;
import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

  private final GetMeService getMe;
  private final UpdateAvatarService updateAvatar;
  private final UpdateMeService updateMe;
  private final RequestOwnerRoleService requestOwner;

  @GetMapping
  public ResponseEntity<UserProfileResponse> getMe() {
    return ResponseEntity.ok(UserProfileResponse.from(getMe.execute()));
  }

  @PutMapping
  public ResponseEntity<UserProfileResponse> updateMe(@Valid @RequestBody UpdateMeRequest request) {
    UpdateMeCommand command =
        new UpdateMeCommand(
            request.displayName(),
            request.description(),
            request.phoneNumber(),
            request.city(),
            request.countryCode(),
            request.preferredSport(),
            request.skillLevel());
    return ResponseEntity.ok(UserProfileResponse.from(updateMe.execute(command)));
  }

  @PatchMapping("/avatar")
  public ResponseEntity<UserProfileResponse> updateAvatar(
      @Valid @RequestBody UpdateAvatarRequest request) {
    ImageUrl imageUrl = new ImageUrl(request.url(), request.publicId());
    return ResponseEntity.ok(UserProfileResponse.from(updateAvatar.execute(imageUrl)));
  }

  @PostMapping("/request-owner")
  public ResponseEntity<Void> requestOwner() {
    requestOwner.execute();
    return ResponseEntity.accepted().build();
  }
}
