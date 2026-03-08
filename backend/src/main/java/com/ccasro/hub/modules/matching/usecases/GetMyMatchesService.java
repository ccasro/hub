package com.ccasro.hub.modules.matching.usecases;

import com.ccasro.hub.modules.matching.domain.MatchRequest;
import com.ccasro.hub.modules.matching.domain.ports.out.MatchRequestRepositoryPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyMatchesService {

  private final MatchRequestRepositoryPort matchRepository;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public List<MatchRequest> execute() {
    return matchRepository.findByPlayerId(currentUser.getUserId());
  }
}
