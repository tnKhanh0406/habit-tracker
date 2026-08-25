package com.prj.habittracker.reward.controller;

import com.prj.habittracker.common.enums.RewardType;
import com.prj.habittracker.reward.dto.request.CreateRewardWithNameRequest;
import com.prj.habittracker.reward.dto.request.RewardRequest;
import com.prj.habittracker.reward.dto.response.RewardResponse;
import com.prj.habittracker.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rewards")
public class RewardController {
    private final RewardService rewardService;

    @GetMapping
    public List<RewardResponse> getRewards(@RequestParam (required = false) RewardType type) {
        return rewardService.getRewardsByType(type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RewardResponse createReward(@RequestBody RewardRequest request) {
        return rewardService.createReward(request);
    }

    @PostMapping("/name")
    @ResponseStatus(HttpStatus.CREATED)
    public RewardResponse createRewardWithName(@RequestBody CreateRewardWithNameRequest request) {
        return rewardService.createRewardWithName(request);
    }

    @PutMapping("/{rewardId}")
    public RewardResponse updateReward(@PathVariable String rewardId, @RequestBody RewardRequest request) {
        return rewardService.updateReward(UUID.fromString(rewardId), request);
    }

    @DeleteMapping("/{rewardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReward(@PathVariable String rewardId) {
        rewardService.deleteReward(UUID.fromString(rewardId));
    }
}
