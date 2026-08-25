package com.prj.habittracker.reward.service;

import com.prj.habittracker.common.enums.RewardType;
import com.prj.habittracker.reward.dto.request.CreateRewardWithNameRequest;
import com.prj.habittracker.reward.dto.request.RewardRequest;
import com.prj.habittracker.reward.dto.response.RewardResponse;

import java.util.List;
import java.util.UUID;

public interface RewardService {
    List<RewardResponse> getRewardsByType(RewardType type);
    RewardResponse createReward(RewardRequest request);
    RewardResponse createRewardWithName(CreateRewardWithNameRequest request);
    RewardResponse updateReward(UUID rewardId, RewardRequest request);
    void deleteReward(UUID rewardId);
}
