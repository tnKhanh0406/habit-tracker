package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.rpg.character.dto.request.AllocateStatRequest;
import com.prj.habittracker.rpg.character.dto.response.AllocateStatResponse;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;

import java.util.UUID;

public interface PlayerCharacterService {
    CharacterResponse getCharacter();
    CharacterResponse createCharacter(UUID userId);
    AllocateStatResponse allocateStat(AllocateStatRequest request);
}
