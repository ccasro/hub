package com.ccasro.hub.modules.iam.infrastructure.persistence;

import com.ccasro.hub.modules.iam.domain.valueobjects.OwnerRequestStatus;
import com.ccasro.hub.modules.matching.infrastructure.persistence.EligiblePlayerProjection;
import com.ccasro.hub.shared.domain.security.UserRole;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserProfileJpaRepository extends JpaRepository<UserProfileEntity, UUID> {

  Optional<UserProfileEntity> findByAuth0Id(String auth0Id);

  @Modifying(clearAutomatically = true)
  @Query(
      value =
          """
      UPDATE user_profile
      SET no_show_count      = no_show_count + 1,
          match_banned_until = CASE
                                 WHEN no_show_count + 1 >= :threshold THEN :bannedUntil
                                 ELSE match_banned_until
                               END,
          updated_at         = :now
      WHERE id IN (:ids)
      """,
      nativeQuery = true)
  void batchConfirmNoShows(
      @Param("ids") Set<UUID> ids,
      @Param("threshold") int threshold,
      @Param("bannedUntil") Instant bannedUntil,
      @Param("now") Instant now);

  @Modifying(clearAutomatically = true)
  @Query(
      value =
          """
      UPDATE user_profile
      SET last_match_cancelled_at = :now, updated_at = :now
      WHERE id = :id
        AND (last_match_cancelled_at IS NULL
             OR last_match_cancelled_at < :cooldownThreshold)
      """,
      nativeQuery = true)
  int tryRecordMatchCancellation(
      @Param("id") UUID id,
      @Param("now") Instant now,
      @Param("cooldownThreshold") Instant cooldownThreshold);

  @Query(
      value =
          """
      SELECT GREATEST(0, CEIL(
          EXTRACT(EPOCH FROM (last_match_cancelled_at + INTERVAL '24 hours' - NOW())) / 3600
      ))::BIGINT
      FROM user_profile
      WHERE id = :id AND last_match_cancelled_at IS NOT NULL
      """,
      nativeQuery = true)
  Optional<Long> findCooldownHoursRemaining(@Param("id") UUID id);

  @Query("select u.id from UserProfileEntity u where u.auth0Id = :auth0Id")
  Optional<UUID> findIdByAuth0Id(@Param("auth0Id") String auth0Id);

  @Query("select u.id as id, u.email as email from UserProfileEntity u where u.id in :ids")
  List<UserEmailProjection> findEmailsByIds(@Param("ids") Set<UUID> ids);

  List<UserProfileEntity> findByOwnerRequestStatus(OwnerRequestStatus status);

  long countByRole(UserRole role);

  long countByOwnerRequestStatus(OwnerRequestStatus status);

  @Query(
      value =
          """
    SELECT
        u.id::text                       AS id,
        u.email                          AS email,
        u.display_name                   AS displayName,
        u.skill_level                    AS skillLevel,
        ST_Y(c.location::geometry)       AS cityLat,
        ST_X(c.location::geometry)       AS cityLng,
        u.match_notifications_enabled    AS matchNotificationsEnabled
    FROM user_profile u
    JOIN city c ON c.id = u.city_id
    WHERE u.role = 'PLAYER'
      AND u.active = true
      AND u.match_notifications_enabled = true
      AND u.id::text != :excludeUserId
      AND (:skillLevel = 'ANY' OR u.skill_level = :skillLevel)
      AND ST_DWithin(
            c.location,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radiusMeters
          )
    """,
      nativeQuery = true)
  List<EligiblePlayerProjection> findEligiblePlayers(
      @Param("lat") double lat,
      @Param("lng") double lng,
      @Param("radiusMeters") double radiusMeters,
      @Param("excludeUserId") String excludeUserId,
      @Param("skillLevel") String skillLevel);

  @Query(
      value =
          """
    SELECT COUNT(*)
    FROM user_profile u
    JOIN city c ON c.id = u.city_id
    WHERE u.role = 'PLAYER'
      AND u.active = true
      AND u.match_notifications_enabled = true
      AND (:skillLevel = 'ANY' OR u.skill_level = :skillLevel)
      AND ST_DWithin(
            c.location,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radiusMeters
          )
    """,
      nativeQuery = true)
  int countEligiblePlayers(
      @Param("lat") double lat,
      @Param("lng") double lng,
      @Param("radiusMeters") double radiusMeters,
      @Param("skillLevel") String skillLevel);
}
