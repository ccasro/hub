package com.ccasro.hub.modules.matching.domain.events;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import java.util.List;

public record MatchInvitationsEvent(MatchRequest matchRequest, List<String> emails) {}
