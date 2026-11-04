package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;

public interface CharacterResponseFactory {
    CharacterResponse create(PlayerCharacterEntity character);
}
