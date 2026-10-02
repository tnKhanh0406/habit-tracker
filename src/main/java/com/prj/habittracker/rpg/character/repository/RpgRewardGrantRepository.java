package com.prj.habittracker.rpg.character.repository;

import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.rpg.character.entity.RpgRewardGrant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RpgRewardGrantRepository extends JpaRepository<RpgRewardGrant, UUID> {
    Optional<RpgRewardGrant> findBySourceTypeAndSourceId(RewardSourceType sourceType, UUID sourceId);
}
