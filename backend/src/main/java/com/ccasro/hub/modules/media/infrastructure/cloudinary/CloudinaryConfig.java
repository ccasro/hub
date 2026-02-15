package com.ccasro.hub.modules.media.infrastructure.cloudinary;

import com.cloudinary.Cloudinary;
import java.util.HashMap;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CloudinaryProps.class)
public class CloudinaryConfig {

  @Bean
  public Cloudinary cloudinary(CloudinaryProps props) {
    var config = new HashMap<String, Object>();
    config.put("cloud_name", props.cloudName());
    config.put("api_key", props.apiKey());
    config.put("api_secret", props.apiSecret());

    return new Cloudinary(config);
  }
}
