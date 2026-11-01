package com.prj.habittracker.rpg.skill.effect.instant;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.springframework.stereotype.Component;

@Component
public class HealHpEffectHandler implements InstantSkillEffectHandler {
    @Override
    public SkillEffectType supports() {
        return SkillEffectType.HEAL_HP;
    }

    @Override
    public void apply(SkillEffectEntity effect, PlayerCharacterEntity character) {
        int heal = effect.getValue().intValue();
        character.setHp(Math.min(character.getMaxHp(), character.getHp() + heal));
    }
}
