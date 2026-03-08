package com.ccasro.hub.modules.matching.domain.ports.out;

import com.ccasro.hub.modules.matching.domain.MatchInvitation;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import java.util.List;

public interface MatchNotificationPort {
  void sendMatchInvitations(MatchRequest matchRequest, List<MatchInvitation> invitations);

  void notifyMatchFull(MatchRequest matchRequest, List<String> playerEmails);

  void notifyMatchCancelled(MatchRequest matchRequest, List<String> playerEmails);

  void notifyPlayerAbsence(MatchRequest matchRequest, List<String> remainingPlayerEmails);
}
