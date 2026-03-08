package com.ccasro.hub.modules.iam.infrastructure.api.dto;

import com.ccasro.hub.modules.iam.usecases.UpdateMeCommand;
import com.ccasro.hub.shared.domain.valueobjects.ImageUrl;

public final class MeCommandMapper {

  private MeCommandMapper() {}

  public static UpdateMeCommand toUpdateMeCommand(UpdateMeRequest request) {
    return new UpdateMeCommand(
        request.displayName(),
        request.description(),
        request.phoneNumber(),
        request.city(),
        request.countryCode(),
        request.preferredSport(),
        request.skillLevel(),
        request.matchNotificationsEnabled());
  }

  public static ImageUrl toImageUrl(UpdateAvatarRequest request) {
    return new ImageUrl(request.url(), request.publicId());
  }
}
