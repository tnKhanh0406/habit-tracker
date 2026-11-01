package com.prj.habittracker.rpg.skill.repository;

import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillCastRepository extends JpaRepository<SkillCastEntity, UUID> {
    Optional<SkillCastEntity> findByUserIdAndIdempotencyKey(UUID userId, UUID idempotencyKey);

    Optional<SkillCastEntity> findFirstByUserIdAndSkillIdOrderByCastAtDesc(UUID userId, UUID skillId);

    @Query("""
        SELECT CASE WHEN COUNT(cast) > 0 THEN true ELSE false END
        FROM SkillCastEntity cast
        WHERE cast.userId = :userId
        AND cast.skill.id = :skillId
        AND cast.status = com.prj.habittracker.common.enums.SkillCastStatus.ACTIVE
        AND (cast.expiresAt IS NULL OR cast.expiresAt > :now)
    """)
    boolean existsActive(UUID userId, UUID skillId, OffsetDateTime now);

    @EntityGraph(attributePaths = {"skill", "skill.effects"})
    @Query("""
        SELECT DISTINCT cast FROM SkillCastEntity cast
        JOIN cast.skill skill
        WHERE cast.userId = :userId
        AND cast.status = com.prj.habittracker.common.enums.SkillCastStatus.ACTIVE
        AND (cast.expiresAt IS NULL OR cast.expiresAt > :now)
        ORDER BY cast.castAt ASC
    """)
    List<SkillCastEntity> findAllActive(UUID userId, OffsetDateTime now);
}
