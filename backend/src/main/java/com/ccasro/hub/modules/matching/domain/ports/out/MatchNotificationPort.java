package com.ccasro.hub.modules.matching.domain.ports.out;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import java.util.List;

public interface MatchNotificationPort {
  void sendMatchInvitations(MatchRequest matchRequest, List<String> playerEmails);

  void notifyMatchFull(MatchRequest matchRequest, List<String> playerEmails);

  void notifyMatchCancelled(MatchRequest matchRequest, List<String> playerEmails);
}
