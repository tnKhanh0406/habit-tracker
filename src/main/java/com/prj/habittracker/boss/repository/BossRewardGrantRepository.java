package com.prj.habittracker.boss.repository;

import com.prj.habittracker.boss.entity.BossRewardGrantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BossRewardGrantRepository extends JpaRepository<BossRewardGrantEntity, UUID> {
    boolean existsByBattleIdAndUserId(UUID battleId, UUID userId);
}
