package com.ccasro.hub.modules.matching.infrastructure.api.dto;

import com.ccasro.hub.modules.matching.domain.PlayerTeam;
import jakarta.validation.constraints.NotNull;

public record JoinMatchRequestRequest(@NotNull PlayerTeam team) {}
