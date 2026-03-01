package com.ccasro.hub.modules.iam.infrastructure.api;

import com.ccasro.hub.modules.iam.infrastructure.api.dto.MeCommandMapper;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.UpdateAvatarRequest;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.UpdateMeRequest;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.UserProfileResponse;
import com.ccasro.hub.modules.iam.usecases.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "User profile", description = "User profile management")
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
    UpdateMeCommand command = MeCommandMapper.toUpdateMeCommand(request);
    return ResponseEntity.ok(UserProfileResponse.from(updateMe.execute(command)));
  }

  @PatchMapping("/avatar")
  public ResponseEntity<UserProfileResponse> updateAvatar(
      @Valid @RequestBody UpdateAvatarRequest request) {
    return ResponseEntity.ok(
        UserProfileResponse.from(updateAvatar.execute(MeCommandMapper.toImageUrl(request))));
  }

  @PostMapping("/request-owner")
  public ResponseEntity<Void> requestOwner() {
    requestOwner.execute();
    return ResponseEntity.accepted().build();
  }
}
