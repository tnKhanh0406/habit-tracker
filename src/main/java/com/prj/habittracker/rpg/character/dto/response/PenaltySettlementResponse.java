package com.prj.habittracker.rpg.character.dto.response;

public record PenaltySettlementResponse(
        int hpDelta,
        int currentHp,
        int missedHabits,
        int overdueTodos
) {
}