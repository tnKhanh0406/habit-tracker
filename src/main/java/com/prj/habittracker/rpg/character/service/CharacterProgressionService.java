package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;

import java.util.UUID;

public interface CharacterProgressionService {
    CharacterResponse getCharacter(UUID userId);
    CharacterResponse applyHpDamage(UUID userId, int amount);
    CharacterResponse applyReward(UUID userId, long exp, long coin);
    CharacterResponse reverseReward(UUID userId, long exp, long coin);
}
