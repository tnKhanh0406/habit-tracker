package com.prj.habittracker.inventory.effect;

import com.prj.habittracker.common.enums.ConsumableEffectType;
import com.prj.habittracker.item.entity.ConsumableDefinitionEntity;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class RestoreMpConsumableHandler implements ConsumableEffectHandler {
    public ConsumableEffectType supports() { return ConsumableEffectType.RESTORE_MP; }

    public void apply(ConsumableDefinitionEntity definition, PlayerCharacterEntity character) {
        if (character.getMp() >= character.getMaxMp()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "MP is already full");
        }
        character.setMp(Math.min(character.getMaxMp(), character.getMp() + definition.getEffectValue().intValue()));
    }
}
