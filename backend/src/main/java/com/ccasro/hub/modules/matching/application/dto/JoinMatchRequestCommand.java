package com.ccasro.hub.modules.matching.application.dto;

import com.ccasro.hub.modules.matching.domain.PlayerTeam;
import com.ccasro.hub.modules.matching.domain.valueobjects.InvitationToken;
import com.ccasro.hub.shared.domain.valueobjects.UserId;

public record JoinMatchRequestCommand(InvitationToken token, UserId playerId, PlayerTeam team) {}
