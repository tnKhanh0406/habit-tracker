package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;

public record RewardOperationResult(
        long expDelta,
        long coinDelta,
        int previousLevel,
        CharacterResponse character
) {
}