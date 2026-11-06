package com.prj.habittracker.boss.repository;

import com.prj.habittracker.boss.entity.BossDamageEventEntity;
import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.common.enums.BossDamageStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface BossDamageEventRepository extends JpaRepository<BossDamageEventEntity, UUID> {
    interface ContributorDamage {
        UUID getUserId();
        Long getTotalDamage();
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"battle", "battle.bossDefinition", "party", "user"})
    @Query("SELECT event FROM BossDamageEventEntity event WHERE event.sourceType = :sourceType AND event.sourceId = :sourceId")
    Optional<BossDamageEventEntity> findBySourceForUpdate(RewardSourceType sourceType, UUID sourceId);

    @Query("SELECT event.user.id AS userId, SUM(event.damage) AS totalDamage " +
            "FROM BossDamageEventEntity event " +
            "WHERE event.battle.id = :battleId AND event.status = :status " +
            "GROUP BY event.user.id")
    List<ContributorDamage> findContributors(UUID battleId, BossDamageStatus status);
}
