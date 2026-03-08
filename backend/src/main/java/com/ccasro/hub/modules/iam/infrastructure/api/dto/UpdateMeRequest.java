package com.ccasro.hub.modules.iam.infrastructure.api.dto;

import com.ccasro.hub.modules.iam.domain.valueobjects.SkillLevel;
import com.ccasro.hub.modules.iam.domain.valueobjects.SportPreference;
import jakarta.validation.constraints.NotBlank;

public record UpdateMeRequest(
    @NotBlank String displayName,
    String description,
    String phoneNumber,
    String city,
    String countryCode,
    SportPreference preferredSport,
    SkillLevel skillLevel,
    Boolean matchNotificationsEnabled) {}
