package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.domain.valueobjects.SkillLevel;
import com.ccasro.hub.modules.iam.domain.valueobjects.SportPreference;

public record UpdateMeCommand(
    String displayName,
    String description,
    String phoneNumber,
    String city,
    String countryCode,
    SportPreference preferredSport,
    SkillLevel skillLevel,
    Boolean matchNotificationsEnabled) {}
