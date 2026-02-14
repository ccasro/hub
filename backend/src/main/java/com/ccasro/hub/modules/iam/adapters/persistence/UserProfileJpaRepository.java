package com.ccasro.hub.modules.iam.adapters.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface UserProfileJpaRepository extends JpaRepository<UserProfileEntity, UUID> {

  Optional<UserProfileEntity> findByAuth0Sub(String auth0Sub);

  @Query("select u.id from UserProfileEntity u where u.auth0Sub = :sub")
  Optional<UUID> findIdByAuth0Sub(@Param("sub") String sub);
}
