package com.ccasro.hub.modules.matching.domain.ports.out;

import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Instant;
import java.util.Set;

public interface MatchPenaltyPort {

  void batchConfirmNoShows(Set<UserId> playerIds, int threshold, Instant bannedUntil, Instant now);

  /**
   * Atomically records a cancellation if the cooldown has expired. Time comes from the injected
   * Clock so tests can control it. Returns true if recorded, false if still in cooldown.
   */
  boolean tryRecordMatchCancellation(UserId userId, Instant now, Instant cooldownThreshold);

  /** Returns how many full hours remain in the cooldown (0 if not in cooldown). */
  long getCooldownHoursRemaining(UserId userId);
}
