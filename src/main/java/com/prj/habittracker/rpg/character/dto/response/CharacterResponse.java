package com.prj.habittracker.rpg.character.dto.response;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;

import java.util.UUID;

public record CharacterResponse(
        UUID id,

        CharacterClass characterClass,

        int level,
        long exp,
        long nextLevelExp,

        int hp,
        int maxHp,

        int mp,
        int maxMp,

        long coin,
        long gem,

        int strength,
        int intelligence,
        int vitality,
        int agility,

        int statPoints
) {
    public static CharacterResponse from(
            PlayerCharacterEntity character,
            long nextLevelExp
    ) {

        return new CharacterResponse(
                character.getId(),

                character.getCharacterClass(),

                character.getLevel(),
                character.getExp(),
                nextLevelExp,

                character.getHp(),
                character.getMaxHp(),

                character.getMp(),
                character.getMaxMp(),

                character.getCoin(),
                character.getGem(),

                character.getStrength(),
                character.getIntelligence(),
                character.getVitality(),
                character.getAgility(),

                character.getStatPoints()
        );
    }
}
