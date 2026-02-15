package com.ccasro.hub.modules.iam.api.dto;

public record MeResponse(
    String id, String email, String displayName, String avatarPublicId, String avatarUrl) {}
