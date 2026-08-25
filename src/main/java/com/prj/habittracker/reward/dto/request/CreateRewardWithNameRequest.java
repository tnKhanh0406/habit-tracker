package com.prj.habittracker.reward.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateRewardWithNameRequest(
        @NotBlank
        String name
) {
}
