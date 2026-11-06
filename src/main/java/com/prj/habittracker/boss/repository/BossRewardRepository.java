package com.prj.habittracker.boss.repository;

import com.prj.habittracker.boss.entity.BossRewardEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BossRewardRepository extends JpaRepository<BossRewardEntity, UUID> {
    @EntityGraph(attributePaths = "bossDefinition")
    List<BossRewardEntity> findAllByBossDefinitionIdInOrderBySortOrderAsc(Collection<UUID> bossDefinitionIds);
    List<BossRewardEntity> findAllByBossDefinitionIdOrderBySortOrderAsc(UUID bossDefinitionId);
}
