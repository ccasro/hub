package com.ccasro.hub.modules.security.config;

import com.ccasro.hub.modules.iam.application.AuthPrincipal;
import com.ccasro.hub.modules.iam.application.ports.EnsureLocalUserUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class EnsureLocalUserFilter extends OncePerRequestFilter {

  private final EnsureLocalUserUseCase ensureLocalUser;

  public EnsureLocalUserFilter(EnsureLocalUserUseCase ensureLocalUser) {
    this.ensureLocalUser = ensureLocalUser;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();

      ensureLocalUser.ensure(new AuthPrincipal(jwt.getSubject()), jwt.getTokenValue());
    }
    chain.doFilter(request, response);
  }
}
