package com.prj.habittracker.rpg.skill.effect.instant;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.service.CharacterStatService;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestoreManaEffectHandler implements InstantSkillEffectHandler {
    private final CharacterStatService characterStatService;
    @Override
    public SkillEffectType supports() {
        return SkillEffectType.RESTORE_MANA;
    }

    @Override
    public void apply(SkillEffectEntity effect, PlayerCharacterEntity character) {
        int restoredMana = effect.getValue().intValue();
        character.setMp(Math.min(characterStatService.calculate(character).maxMp(), character.getMp() + restoredMana));
    }
}
