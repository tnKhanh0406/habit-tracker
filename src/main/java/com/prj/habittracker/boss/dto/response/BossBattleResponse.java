package com.prj.habittracker.boss.dto.response;

import com.prj.habittracker.common.enums.BossBattleStatus;
import com.prj.habittracker.common.enums.BossDifficulty;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BossBattleResponse(
        UUID id,
        UUID partyId,
        UUID bossDefinitionId,
        String bossCode,
        String bossName,
        BossDifficulty difficulty,
        BossBattleStatus status,
        int currentHp,
        int maxHp,
        UUID startedByUserId,
        String startedByUsername,
        OffsetDateTime startedAt
) {}
