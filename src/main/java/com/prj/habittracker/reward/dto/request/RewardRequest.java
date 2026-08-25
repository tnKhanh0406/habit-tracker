package com.prj.habittracker.reward.dto.request;

import com.prj.habittracker.common.enums.RewardType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RewardRequest(
    @NotBlank
    String name,
    String description,
    @NotBlank
    String icon,
    @NotNull @Min(1)
    Integer cost,
    @NotNull
    RewardType type
) {
}
