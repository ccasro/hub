package com.ccasro.hub.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager manager = new CaffeineCacheManager();

    manager.registerCustomCache(
        "venues",
        Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(200).build());

    manager.registerCustomCache(
        "venue-detail",
        Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(500).build());

    manager.registerCustomCache(
        "venues-with-count",
        Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(200).build());

    manager.registerCustomCache(
        "slots",
        Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).maximumSize(1000).build());

    manager.registerCustomCache(
        "cities",
        Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).maximumSize(100).build());

    manager.registerCustomCache(
        "venues-nearby",
        Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).maximumSize(500).build());

    return manager;
  }
}
