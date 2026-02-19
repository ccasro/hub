package com.ccasro.hub.modules.iam.infrastructure.persistence;

import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface UserProfileJpaRepository extends JpaRepository<UserProfileEntity, UUID> {

  Optional<UserProfileEntity> findByAuth0Id(String auth0Id);

  @Query("select u.id from UserProfileEntity u where u.auth0Id = :auth0Id")
  Optional<UUID> findIdByAuth0Id(@Param("auth0Id") String auth0Id);

  List<UserProfileEntity> findByOwnerRequestStatus(OwnerRequestStatus status);
}
