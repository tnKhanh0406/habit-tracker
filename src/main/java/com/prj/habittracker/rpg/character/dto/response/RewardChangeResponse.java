package com.prj.habittracker.rpg.character.dto.response;

public record RewardChangeResponse(
        long expDelta,
        long coinDelta,
        long currentExp,
        long currentCoin,
        int currentLevel,
        boolean levelUp,
        boolean levelDown
) {
}