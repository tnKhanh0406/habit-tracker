package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.model.EffectiveCharacterStats;

public interface CharacterStatService {
    EffectiveCharacterStats calculate(PlayerCharacterEntity character);
    void clampResources(PlayerCharacterEntity character);
}
