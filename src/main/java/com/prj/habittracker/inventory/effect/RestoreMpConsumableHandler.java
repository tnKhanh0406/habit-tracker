package com.prj.habittracker.inventory.effect;

import com.prj.habittracker.common.enums.ConsumableEffectType;
import com.prj.habittracker.item.entity.ConsumableDefinitionEntity;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.service.CharacterStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class RestoreMpConsumableHandler implements ConsumableEffectHandler {
    private final CharacterStatService characterStatService;
    public ConsumableEffectType supports() { return ConsumableEffectType.RESTORE_MP; }

    public void apply(ConsumableDefinitionEntity definition, PlayerCharacterEntity character) {
        int maxMp = characterStatService.calculate(character).maxMp();
        if (character.getMp() >= maxMp) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "MP is already full");
        }
        character.setMp(Math.min(maxMp, character.getMp() + definition.getEffectValue().intValue()));
    }
}
