package com.prj.habittracker.reward.mapper;

import com.prj.habittracker.reward.dto.response.RewardResponse;
import com.prj.habittracker.reward.entity.RewardEntity;
import org.springframework.stereotype.Component;

@Component
public class RewardMapper {
    public RewardResponse toResponse(RewardEntity entity) {
        return new RewardResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getIcon(),
                entity.getCost(),
                entity.getType()
        );
    }
}
