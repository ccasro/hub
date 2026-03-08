package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DetectNoShowsJob {

  private static final Duration WINDOW = Duration.ofMinutes(90);
  private static final Duration JOB_INTERVAL = Duration.ofMinutes(15);

  private final MatchRequestRepositoryPort matchRepository;
  private final UserProfileRepositoryPort userRepository;
  private final Clock clock;

  @Scheduled(fixedDelayString = "PT15M")
  @Transactional
  public void execute() {
    Instant now = clock.instant();
    Instant from = now.minus(WINDOW);
    Instant to = now.minus(JOB_INTERVAL);

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
    List<UserId> noShows =
        match.getPlayers().stream()
            .filter(p -> !p.isCheckedIn())
            .map(p -> p.getPlayerId())
            .toList();

    if (noShows.isEmpty()) return;

    log.info("Match {} has {} no-show(s)", match.getId().value(), noShows.size());

    for (UserId playerId : noShows) {
      userRepository
          .findById(playerId)
          .ifPresentOrElse(
              profile -> {
                profile.confirmNoShow(clock);
                userRepository.save(profile);
                log.info(
                    "No-show confirmed for player {} (total: {})",
                    playerId.value(),
                    profile.getNoShowCount());
              },
              () -> log.warn("Player {} not found when processing no-show", playerId.value()));
    }
  }
}
