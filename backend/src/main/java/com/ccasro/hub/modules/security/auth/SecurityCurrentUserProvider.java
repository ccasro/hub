package com.ccasro.hub.modules.security.auth;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.modules.iam.domain.UserId;
import com.ccasro.hub.modules.iam.domain.ports.UserProfileRepositoryPort;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class SecurityCurrentUserProvider implements CurrentUserProvider {

  private final UserProfileRepositoryPort users;

  private String cachedSubject;
  private Set<String> cachedAuthorities;
  private UserId cachedUserId;

  public SecurityCurrentUserProvider(UserProfileRepositoryPort users) {
    this.users = users;
  }

  @Override
  public String getSub() {
    ensureLoaded();
    return cachedSubject;
  }

  @Override
  public Set<String> authorities() {
    ensureLoaded();
    return cachedAuthorities;
  }

  @Override
  public UserId getUserId() {
    ensureLoaded();
    if (cachedUserId != null) return cachedUserId;

    cachedUserId =
        users
            .findIdByAuth0Sub(cachedSubject)
            .orElseThrow(() -> new IllegalStateException("Local user not found for sub"));

    return cachedUserId;
  }

  private void ensureLoaded() {
    if (cachedSubject != null && cachedAuthorities != null) return;

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
      throw new IllegalStateException("No authenticated JWT user");
    }

    cachedSubject = jwtAuth.getToken().getSubject();

    cachedAuthorities =
        auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toUnmodifiableSet());
  }
}
