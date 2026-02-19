package com.ccasro.hub.modules.media.infrastructure.api;

import com.ccasro.hub.modules.media.application.usecase.GenerateUploadSignatureUseCase;
import com.ccasro.hub.modules.media.infrastructure.api.dto.UploadSignatureRequest;
import com.ccasro.hub.modules.media.infrastructure.api.dto.UploadSignatureResponse;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uploads")
public class UploadSignatureController {

  private final CurrentUserProvider currentUser;
  private final GenerateUploadSignatureUseCase useCase;

  public UploadSignatureController(
      CurrentUserProvider currentUser, GenerateUploadSignatureUseCase useCase) {
    this.currentUser = currentUser;
    this.useCase = useCase;
  }

  @PostMapping("/signature")
  public UploadSignatureResponse signature(@Valid @RequestBody UploadSignatureRequest req) {
    String principalId = sanitizeSub(currentUser.getSub());
    return useCase.request(principalId, req);
  }

  static String sanitizeSub(String raw) {
    if (raw == null || raw.isBlank()) throw new IllegalArgumentException("sub is required");
    return raw.replace('|', '_').replace(':', '_');
  }
}
