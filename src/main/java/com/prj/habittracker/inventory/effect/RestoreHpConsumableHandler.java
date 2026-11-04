package com.prj.habittracker.inventory.effect;

import com.prj.habittracker.common.enums.ConsumableEffectType;
import com.prj.habittracker.item.entity.ConsumableDefinitionEntity;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class RestoreHpConsumableHandler implements ConsumableEffectHandler {
    public ConsumableEffectType supports() { return ConsumableEffectType.RESTORE_HP; }

    public void apply(ConsumableDefinitionEntity definition, PlayerCharacterEntity character) {
        if (character.getHp() >= character.getMaxHp()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "HP is already full");
        }
        character.setHp(Math.min(character.getMaxHp(), character.getHp() + definition.getEffectValue().intValue()));
    }
}
