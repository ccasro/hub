package com.ccasro.hub.modules.media.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.ccasro.hub.modules.media.application.FolderPolicy;
import com.ccasro.hub.modules.media.application.mapper.UploadContextMapper;
import com.ccasro.hub.modules.media.application.ports.MediaStoragePort;
import com.ccasro.hub.modules.media.domain.UploadContext;
import com.ccasro.hub.modules.media.domain.UploadPurpose;
import com.ccasro.hub.modules.media.infrastructure.api.dto.UploadSignatureRequest;
import com.ccasro.hub.modules.media.infrastructure.api.dto.UploadSignatureResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

  @ParameterizedTest
  @EnumSource(UploadPurpose.class)
  void request_generalized_signsExpectedParams_parameterized(UploadPurpose purpose) {
    String principalId = "auth0|123";

    UploadSignatureRequest req;
    String expectedFolder;
    boolean expectedOverwrite;

    switch (purpose) {
      case AVATAR -> {
        req = new UploadSignatureRequest(purpose, null, null);
        expectedFolder = "avatars/auth0_123";
        expectedOverwrite = true;
      }
      case VENUE_IMAGE -> {
        req = new UploadSignatureRequest(purpose, "venue-42", null);
        expectedFolder = "venues/venue-42";
        expectedOverwrite = false;
      }
      case RESOURCE_IMAGE -> {
        req = new UploadSignatureRequest(purpose, null, "res-99");
        expectedFolder = "resources/res-99";
        expectedOverwrite = false;
      }
      default -> throw new IllegalStateException("Unexpected purpose: " + purpose);
    }

    UploadContext ctx = UploadContext.forAvatar(principalId);
    when(mapper.toContext(eq(principalId), eq(req))).thenReturn(ctx);

    // 👈 Clave: usamos any() para no depender del objeto exacto
    when(folderPolicy.resolveFolder(eq(purpose), any())).thenReturn(expectedFolder);

    // 👈 Clave: devolvemos SignedUploadParams siempre, sin importar argumentos
    when(mediaStorage.createSignedUploadParams(anyLong(), anyString(), anyString(), anyBoolean()))
        .thenAnswer(
            invocation -> {
              long ts = invocation.getArgument(0);
              String folder = invocation.getArgument(1);
              String publicId = invocation.getArgument(2);
              boolean overwrite = invocation.getArgument(3);
              return new MediaStoragePort.SignedUploadParams(
                  "cloudinary", "cloud", "key", ts, folder, publicId, overwrite, "sig");
            });

    UploadSignatureResponse res = useCase.request(principalId, req);

    assertEquals("cloudinary", res.provider());
    assertEquals("cloud", res.cloudName());
    assertEquals("key", res.apiKey());
    assertEquals(expectedFolder, res.folder());
    assertEquals(expectedOverwrite, res.overwrite());
    assertEquals("sig", res.signature());

    if (purpose == UploadPurpose.AVATAR) {
      assertEquals("avatar", res.publicId());
    } else {
      assertNotNull(res.publicId());
      assertFalse(res.publicId().isBlank());
    }

    ArgumentCaptor<Long> tsCaptor = ArgumentCaptor.forClass(Long.class);
    verify(mediaStorage)
        .createSignedUploadParams(tsCaptor.capture(), anyString(), anyString(), anyBoolean());
    assertTrue(tsCaptor.getValue() > 0);
  }

  @Test
  void request_blankPrincipal_throws() {
    UploadSignatureRequest req = new UploadSignatureRequest(UploadPurpose.AVATAR, null, null);
    assertThrows(IllegalArgumentException.class, () -> useCase.request(" ", req));
  }

  @Test
  void request_nullPurpose_throws() {
    assertThrows(
        IllegalArgumentException.class,
        () -> useCase.request("auth0|123", new UploadSignatureRequest(null, null, null)));
  }
}
