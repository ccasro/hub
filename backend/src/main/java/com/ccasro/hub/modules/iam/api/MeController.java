package com.ccasro.hub.modules.iam.api;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.modules.iam.api.dto.MeResponse;
import com.ccasro.hub.modules.iam.api.dto.UpdateAvatarRequest;
import com.ccasro.hub.modules.iam.application.usecases.GetMeUseCase;
import com.ccasro.hub.modules.iam.application.usecases.UpdateAvatarRequestUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class MeController {

  private final GetMeUseCase getMe;
  private final UpdateAvatarRequestUseCase updAvatar;
  private final CurrentUserProvider currentUser;

  public MeController(
      GetMeUseCase getMe, UpdateAvatarRequestUseCase updAvatar, CurrentUserProvider currentUser) {
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
