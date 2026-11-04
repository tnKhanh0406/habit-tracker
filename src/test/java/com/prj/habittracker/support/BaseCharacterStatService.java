package com.prj.habittracker.support;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.model.EffectiveCharacterStats;
import com.prj.habittracker.rpg.character.service.CharacterStatService;

import java.math.BigDecimal;

public class BaseCharacterStatService implements CharacterStatService {
    @Override
    public EffectiveCharacterStats calculate(PlayerCharacterEntity character) {
        return new EffectiveCharacterStats(value(character.getStrength()), value(character.getIntelligence()),
                value(character.getVitality()), value(character.getAgility()), value(character.getMaxHp()),
                value(character.getMaxMp()),
                BigDecimal.ZERO);
    }

    @Override
    public void clampResources(PlayerCharacterEntity character) {
        character.setHp(Math.min(character.getHp(), character.getMaxHp()));
        character.setMp(Math.min(character.getMp(), character.getMaxMp()));
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }
}
