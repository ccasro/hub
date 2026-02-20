package com.ccasro.hub.modules.iam.usecases;

import com.ccasro.hub.modules.iam.application.dto.AuthPrincipal;
import com.ccasro.hub.modules.iam.application.ports.in.EnsureLocalUserUseCase;
import com.ccasro.hub.modules.iam.domain.UserProfile;
import com.ccasro.hub.modules.iam.domain.ports.out.UserProfileRepositoryPort;
import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfo;
import com.ccasro.hub.modules.iam.infrastructure.auth0.Auth0UserInfoClient;
import com.ccasro.hub.shared.domain.security.UserRole;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnsureLocalUserService implements EnsureLocalUserUseCase {

  private final UserProfileRepositoryPort users;
  private final Auth0UserInfoClient auth0Client;
  private final Clock clock;

  @Value("${app.admin.email:}")
  private String adminEmail;

  @Override
  @Transactional
  public UserProfile ensure(AuthPrincipal principal, String accessToken) {

    Auth0Id auth0Id = new Auth0Id(principal.sub());

    return users
        .findByAuth0Id(auth0Id)
        .map(
            profile -> {
              profile.recordLogin(clock);
              return users.save(profile);
            })
        .orElseGet(
            () -> {
              Auth0UserInfo ui = auth0Client.getUserInfo((accessToken));

              if (ui == null || ui.sub() == null || !ui.sub().equals(principal.sub())) {
                throw new IllegalStateException("userinfo sub mismatch");
              }

              UserProfile created = UserProfile.create(auth0Id, clock);

              created.updateEmailFromRaw(ui.email(), clock);
              if (!adminEmail.isBlank() && adminEmail.equalsIgnoreCase(ui.email())) {
                created.changeRole(UserRole.ADMIN, clock);
                log.info("User promoted to ADMIN: {}", adminEmail);
              }

              try {
                return users.save(created);
              } catch (DataIntegrityViolationException e) {
                UserProfile existing = users.findByAuth0Id(auth0Id).orElseThrow(() -> e);

                existing.recordLogin(clock);
                existing.updateEmailFromRaw(ui.email(), clock);

                if (!adminEmail.isBlank()
                    && adminEmail.equalsIgnoreCase(ui.email())
                    && existing.getRole() != UserRole.ADMIN) {
                  existing.changeRole(UserRole.ADMIN, clock);
                  log.info("User promoted to ADMIN: {}", adminEmail);
                }

                return users.save(existing);
              }
            });
  }
}
