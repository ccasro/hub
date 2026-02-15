package com.ccasro.hub.modules.security.auth;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
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

  private String cachedSubject;
  private Set<String> cachedAuthorities;

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
