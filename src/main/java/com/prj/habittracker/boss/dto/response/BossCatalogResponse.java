package com.prj.habittracker.boss.dto.response;

import com.prj.habittracker.common.enums.BossAvailability;
import com.prj.habittracker.common.enums.BossDifficulty;

import java.util.UUID;

public record BossCatalogResponse(
        UUID id,
        String code,
        String name,
        String description,
        BossDifficulty difficulty,
        int sequenceOrder,
        int baseHp,
        String icon,
        BossAvailability availability,
        String unavailableReason
) {}
