package com.ccasro.hub.modules.iam.infrastructure.api;

import com.ccasro.hub.modules.iam.infrastructure.api.dto.MeResponse;
import com.ccasro.hub.modules.iam.infrastructure.api.dto.UpdateAvatarRequest;
import com.ccasro.hub.modules.iam.usecases.GetMeService;
import com.ccasro.hub.modules.iam.usecases.UpdateAvatarService;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class MeController {

  private final GetMeService getMe;
  private final UpdateAvatarService updAvatar;
  private final CurrentUserProvider currentUser;

  public MeController(
      GetMeService getMe, UpdateAvatarService updAvatar, CurrentUserProvider currentUser) {
    this.getMe = getMe;
    this.updAvatar = updAvatar;
    this.currentUser = currentUser;
  }

  @GetMapping
  public MeResponse me() {
    var u = getMe.get(currentUser.getSub());
    return new MeResponse(
        u.getId().toString(),
        u.getEmail(),
        u.getDisplayName(),
        u.getAvatarPublicId(),
        u.getAvatarUrl());
  }

  @PutMapping("/avatar")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateAvatar(@Valid @RequestBody UpdateAvatarRequest req) {
    updAvatar.update(currentUser.getSub(), req);
  }
}
