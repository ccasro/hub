package com.ccasro.hub.modules.media.api;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ccasro.hub.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class UploadSignatureControllerIT extends BaseIT {

  @Test
  void postSignature_avatar_returnsSignedParams() throws Exception {
    mvc.perform(
            post("/media/uploads/signature")
                .header("Authorization", bearer(userToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"purpose\":\"AVATAR\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.folder", startsWith("avatars/")));
  }

  @Test
  void postSignature_withoutToken_isUnauthorized() throws Exception {
    mvc.perform(
            post("/media/uploads/signature")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"purpose\": \"AVATAR\" }"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void postSignature_missingPurpose_isBadRequest() throws Exception {
    mvc.perform(
            post("/media/uploads/signature")
                .header("Authorization", bearer(userToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest());
  }
}
