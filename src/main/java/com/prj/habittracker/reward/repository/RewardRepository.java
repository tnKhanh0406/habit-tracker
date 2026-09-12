package com.prj.habittracker.reward.repository;

import com.prj.habittracker.common.enums.RewardType;
import com.prj.habittracker.reward.dto.response.RewardResponse;
import com.prj.habittracker.reward.entity.RewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RewardRepository extends JpaRepository<RewardEntity, UUID> {
    @Query("""
        SELECT new com.prj.habittracker.reward.dto.response.RewardResponse(
            r.id,
            r.name,
            r.description,
            r.icon,
            r.cost,
            r.type
        )
        FROM RewardEntity r
        WHERE (:type is NULL OR r.type = :type) AND r.user.id = :userId
    """)
    List<RewardResponse> getRewardsByType(RewardType type, UUID userId);

    Optional<RewardEntity> findByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("DELETE FROM RewardEntity r WHERE r.id = :rewardId AND r.user.id = :userId")
    int deleteByIdAndUserId(UUID rewardId, UUID userId);
}
