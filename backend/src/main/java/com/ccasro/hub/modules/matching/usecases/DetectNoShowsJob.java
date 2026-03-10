package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.infrastructure.config.IamProperties;
import com.ccasro.hub.infrastructure.config.MatchingProperties;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DetectNoShowsJob {

  private final MatchRequestRepositoryPort matchRepository;
  private final UserProfileRepositoryPort userRepository;
  private final MatchingProperties matchingProperties;
  private final IamProperties iamProperties;
  private final Clock clock;

  @Scheduled(fixedDelayString = "${matching.no-show-detection-job-delay:PT15M}")
  @Transactional
  public void execute() {
    Instant now = clock.instant();
    Instant from = now.minus(matchingProperties.getNoShowDetectionWindow());
    Instant to = now.minus(matchingProperties.getNoShowDetectionJobDelay());

    List<MatchRequest> matches = matchRepository.findFullEndedBetween(from, to);
    if (matches.isEmpty()) return;

    log.info("Detecting no-shows for {} matches", matches.size());

    for (MatchRequest match : matches) {
      try {
        processMatch(match);
      } catch (Exception e) {
        log.error(
            "Error detecting no-shows for match {}: {}", match.getId().value(), e.getMessage());
      }
    }
  }

  private void processMatch(MatchRequest match) {
    Set<UserId> noShows =
        match.getPlayers().stream()
            .filter(p -> !p.isCheckedIn())
            .map(p -> p.getPlayerId())
            .collect(Collectors.toSet());

    if (noShows.isEmpty()) return;

    log.info("Match {} has {} no-show(s)", match.getId().value(), noShows.size());

    Instant now = clock.instant();
    Instant bannedUntil = now.plus(iamProperties.getNoShowBanDuration());
    userRepository.batchConfirmNoShows(
        noShows, iamProperties.getNoShowBanThreshold(), bannedUntil, now);

    log.info(
        "No-shows confirmed for {} player(s) in match {}", noShows.size(), match.getId().value());
  }
}
