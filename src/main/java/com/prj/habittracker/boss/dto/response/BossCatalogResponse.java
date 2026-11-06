package com.prj.habittracker.boss.dto.response;

import com.prj.habittracker.common.enums.BossAvailability;
import com.prj.habittracker.common.enums.BossDifficulty;

import java.util.UUID;
import java.util.List;

public record BossCatalogResponse(
        UUID id,
        String code,
        String name,
        String description,
        BossDifficulty difficulty,
        int sequenceOrder,
        int baseHp,
        String icon,
        List<BossRewardResponse> rewards,
        BossAvailability availability,
        String unavailableReason
) {}
