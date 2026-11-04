package com.prj.habittracker.rpg.skill.effect.instant;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.service.CharacterStatService;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HealHpEffectHandler implements InstantSkillEffectHandler {
    private final CharacterStatService characterStatService;
    @Override
    public SkillEffectType supports() {
        return SkillEffectType.HEAL_HP;
    }

    @Override
    public void apply(SkillEffectEntity effect, PlayerCharacterEntity character) {
        int heal = effect.getValue().intValue();
        character.setHp(Math.min(characterStatService.calculate(character).maxHp(), character.getHp() + heal));
    }
}
