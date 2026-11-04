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
public class RestoreHpConsumableHandler implements ConsumableEffectHandler {
    private final CharacterStatService characterStatService;
    public ConsumableEffectType supports() { return ConsumableEffectType.RESTORE_HP; }

    public void apply(ConsumableDefinitionEntity definition, PlayerCharacterEntity character) {
        int maxHp = characterStatService.calculate(character).maxHp();
        if (character.getHp() >= maxHp) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "HP is already full");
        }
        character.setHp(Math.min(maxHp, character.getHp() + definition.getEffectValue().intValue()));
    }
}
