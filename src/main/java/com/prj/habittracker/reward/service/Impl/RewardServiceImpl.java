package com.prj.habittracker.reward.service.Impl;

import com.prj.habittracker.common.enums.RewardType;
import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.reward.dto.request.CreateRewardWithNameRequest;
import com.prj.habittracker.reward.dto.request.RewardRequest;
import com.prj.habittracker.reward.dto.response.RewardResponse;
import com.prj.habittracker.reward.entity.RewardEntity;
import com.prj.habittracker.reward.mapper.RewardMapper;
import com.prj.habittracker.reward.repository.RewardRepository;
import com.prj.habittracker.reward.service.RewardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {
    private final RewardRepository rewardRepository;
    private final CurrentUserService currentUserService;
    private final RewardMapper rewardMapper;

    @Override
    public List<RewardResponse> getRewardsByType(RewardType type) {
        return rewardRepository.getRewardsByType(type, currentUserService.getCurrentUser().getId());
    }

    @Override
    public RewardResponse createReward(RewardRequest request) {
        RewardEntity reward = new RewardEntity();
        reward.setUser(currentUserService.getCurrentUser());
        return setRewardField(request, reward);
    }

    @Override
    public RewardResponse createRewardWithName(CreateRewardWithNameRequest request) {
        RewardEntity reward = new RewardEntity();
        reward.setUser(currentUserService.getCurrentUser());
        reward.setName(request.name());
        RewardEntity saved = rewardRepository.save(reward);
        return rewardMapper.toResponse(saved);
    }

    @Override
    public RewardResponse updateReward(UUID rewardId, RewardRequest request) {
        RewardEntity reward = rewardRepository.findByIdAndUserId(rewardId, currentUserService.getCurrentUserId())
                .orElseThrow(() -> new EntityNotFoundException("Reward not found with id: " + rewardId));
        return setRewardField(request, reward);
    }

    private RewardResponse setRewardField(RewardRequest request, RewardEntity reward) {
        reward.setCost(request.cost());
        reward.setDescription(request.description());
        reward.setIcon(request.icon());
        reward.setName(request.name());
        reward.setType(request.type() == null ? RewardType.CUSTOM : request.type());
        RewardEntity updated = rewardRepository.save(reward);
        return rewardMapper.toResponse(updated);
    }

    @Override
    public void deleteReward(UUID rewardId) {
        int deleted = rewardRepository.deleteByIdAndUserId(rewardId, currentUserService.getCurrentUserId());
        if (deleted == 0) {
            throw new EntityNotFoundException("Reward not found with id: " + rewardId);
        }
    }
}
