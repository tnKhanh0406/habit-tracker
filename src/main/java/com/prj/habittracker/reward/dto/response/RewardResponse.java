package com.prj.habittracker.reward.dto.response;

import com.prj.habittracker.common.enums.RewardType;

import java.util.UUID;

public record RewardResponse(
    UUID id,
    String name,
    String description,
    String icon,
    Integer cost,
    RewardType type
) {
}
