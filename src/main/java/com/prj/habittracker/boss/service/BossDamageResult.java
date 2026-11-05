package com.prj.habittracker.boss.service;

import com.prj.habittracker.common.enums.BossBattleStatus;

import java.util.UUID;

public record BossDamageResult(
        UUID battleId,
        int damageDelta,
        int currentHp,
        BossBattleStatus battleStatus
) {
    public static BossDamageResult noChange() {
        return new BossDamageResult(null, 0, 0, null);
    }
}
