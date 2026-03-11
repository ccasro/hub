package com.ccasro.hub.modules.admin.usecases;

import com.ccasro.hub.modules.admin.domain.ports.out.AdminStatsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PreAuthorize("@authz.isAdmin()")
public class GetAdminStatsService {

  private final AdminStatsPort adminStatsPort;

  public AdminStatsPort.AdminStats execute() {
    return adminStatsPort.fetchStats();
  }
}
