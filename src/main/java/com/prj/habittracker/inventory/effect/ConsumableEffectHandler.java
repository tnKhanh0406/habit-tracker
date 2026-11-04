package com.prj.habittracker.inventory.effect;

import com.prj.habittracker.common.enums.ConsumableEffectType;
import com.prj.habittracker.item.entity.ConsumableDefinitionEntity;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;

public interface ConsumableEffectHandler {
    ConsumableEffectType supports();
    void apply(ConsumableDefinitionEntity definition, PlayerCharacterEntity character);
}
