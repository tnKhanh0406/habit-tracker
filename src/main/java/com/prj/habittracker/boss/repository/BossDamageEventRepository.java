package com.prj.habittracker.boss.repository;

import com.prj.habittracker.boss.entity.BossDamageEventEntity;
import com.prj.habittracker.common.enums.RewardSourceType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BossDamageEventRepository extends JpaRepository<BossDamageEventEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"battle", "battle.bossDefinition", "party", "user"})
    @Query("SELECT event FROM BossDamageEventEntity event WHERE event.sourceType = :sourceType AND event.sourceId = :sourceId")
    Optional<BossDamageEventEntity> findBySourceForUpdate(RewardSourceType sourceType, UUID sourceId);
}
