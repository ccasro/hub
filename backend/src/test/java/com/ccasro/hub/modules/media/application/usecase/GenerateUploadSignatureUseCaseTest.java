package com.ccasro.hub.modules.media.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.ccasro.hub.modules.media.api.dto.UploadSignatureRequest;
import com.ccasro.hub.modules.media.api.dto.UploadSignatureResponse;
import com.ccasro.hub.modules.media.application.FolderPolicy;
import com.ccasro.hub.modules.media.application.MediaStoragePort;
import com.ccasro.hub.modules.media.application.mapper.UploadContextMapper;
import com.ccasro.hub.modules.media.domain.UploadContext;
import com.ccasro.hub.modules.media.domain.UploadPurpose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class GenerateUploadSignatureUseCaseTest {

  private FolderPolicy folderPolicy;
  private MediaStoragePort mediaStorage;
  private UploadContextMapper mapper;

  private GenerateUploadSignatureUseCase useCase;

  @BeforeEach
  void setUp() {
    folderPolicy = mock(FolderPolicy.class);
    mediaStorage = mock(MediaStoragePort.class);
    mapper = mock(UploadContextMapper.class);

    useCase = new GenerateUploadSignatureUseCase(folderPolicy, mediaStorage, mapper);
  }

  @Test
  void request_avatar_signsExpectedParams() {
    String sub = "auth0|123";
    UploadSignatureRequest req = new UploadSignatureRequest(UploadPurpose.AVATAR, null, null, null);

    UploadContext ctx = UploadContext.forAvatar(sub);
    when(mapper.toContext(eq(sub), eq(req))).thenReturn(ctx);

    when(folderPolicy.resolveFolder(eq(UploadPurpose.AVATAR), eq(ctx)))
        .thenReturn("avatars/auth0_123");

    MediaStoragePort.SignedUploadParams params =
        new MediaStoragePort.SignedUploadParams(
            "cloudinary", "cloud", "key", 111L, "avatars/auth0_123", "avatar", true, "sig");

    when(mediaStorage.createSignedUploadParams(
            anyLong(), eq("avatars/auth0_123"), eq("avatar"), eq(true)))
        .thenReturn(params);

    UploadSignatureResponse res = useCase.request(sub, req);

    assertEquals("cloudinary", res.provider());
    assertEquals("cloud", res.cloudName());
    assertEquals("key", res.apiKey());
    assertEquals("avatars/auth0_123", res.folder());
    assertEquals("avatar", res.publicId());
    assertTrue(res.overwrite());
    assertEquals("sig", res.signature());

    ArgumentCaptor<Long> tsCaptor = ArgumentCaptor.forClass(Long.class);
    verify(mediaStorage)
        .createSignedUploadParams(
            tsCaptor.capture(), eq("avatars/auth0_123"), eq("avatar"), eq(true));
    assertTrue(tsCaptor.getValue() > 0);
  }

  @Test
  void request_companyLogo_requiresCompanyId() {
    String sub = "auth0|123";
    UploadSignatureRequest req =
        new UploadSignatureRequest(UploadPurpose.COMPANY_LOGO, null, null, null);

    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> useCase.request(sub, req));
    assertTrue(ex.getMessage().contains("companyId"));
  }

  @Test
  void request_blankPrincipal_throws() {
    UploadSignatureRequest req = new UploadSignatureRequest(UploadPurpose.AVATAR, null, null, null);
    assertThrows(IllegalArgumentException.class, () -> useCase.request(" ", req));
  }

  @Test
  void request_nullPurpose_throws() {
    assertThrows(
        IllegalArgumentException.class,
        () -> useCase.request("auth0|123", new UploadSignatureRequest(null, null, null, null)));
  }
}
