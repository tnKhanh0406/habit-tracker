package com.prj.habittracker.rpg.character.dto.response;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.model.EffectiveCharacterStats;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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

        int baseMaxHp,
        int baseMaxMp,
        int baseStrength,
        int baseIntelligence,
        int baseVitality,
        int baseAgility,
        BigDecimal bossDamagePercent,

        int statPoints,
        OffsetDateTime nextClassChangeAt
) {
    public static CharacterResponse from(
            PlayerCharacterEntity character,
            long nextLevelExp,
            EffectiveCharacterStats stats
    ) {

        return new CharacterResponse(
                character.getId(),

                character.getCharacterClass(),

                character.getLevel(),
                character.getExp(),
                nextLevelExp,

                character.getHp(),
                stats.maxHp(),

                character.getMp(),
                stats.maxMp(),

                character.getCoin(),
                character.getGem(),

                stats.strength(),
                stats.intelligence(),
                stats.vitality(),
                stats.agility(),

                character.getMaxHp(),
                character.getMaxMp(),
                character.getStrength(),
                character.getIntelligence(),
                character.getVitality(),
                character.getAgility(),
                stats.bossDamagePercent(),

                character.getStatPoints(),
                character.getClassChangedAt() == null ? null : character.getClassChangedAt().plusDays(7)
        );
    }
}
