package com.ccasro.hub.modules.iam.application.security;

import com.ccasro.hub.modules.iam.domain.valueobjects.Auth0Id;
import com.ccasro.hub.shared.domain.security.UserRole;
import com.ccasro.hub.shared.domain.valueobjects.UserId;

public record CurrentUserContext(UserId userId, Auth0Id auth0Id, UserRole role) {}
