package com.prj.habittracker.rpg.character.service.impl;

import com.prj.habittracker.rpg.character.config.LevelPolicy;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.service.CharacterResponseFactory;
import com.prj.habittracker.rpg.character.service.CharacterStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterResponseFactoryImpl implements CharacterResponseFactory {
    private final CharacterStatService characterStatService;
    private final LevelPolicy levelPolicy;

    @Override
    public CharacterResponse create(PlayerCharacterEntity character) {
        return CharacterResponse.from(character, levelPolicy.requiredExp(character.getLevel()),
                characterStatService.calculate(character));
    }
}
