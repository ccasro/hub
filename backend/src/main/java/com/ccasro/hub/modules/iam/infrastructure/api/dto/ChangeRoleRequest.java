package com.ccasro.hub.modules.iam.infrastructure.api.dto;

import com.ccasro.hub.shared.domain.security.UserRole;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(@NotNull UserRole role) {}
