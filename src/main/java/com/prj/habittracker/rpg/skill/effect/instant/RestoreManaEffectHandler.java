package com.prj.habittracker.rpg.skill.effect.instant;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.springframework.stereotype.Component;

@Component
public class RestoreManaEffectHandler implements InstantSkillEffectHandler {
    @Override
    public SkillEffectType supports() {
        return SkillEffectType.RESTORE_MANA;
    }

    @Override
    public void apply(SkillEffectEntity effect, PlayerCharacterEntity character) {
        int restoredMana = effect.getValue().intValue();
        character.setMp(Math.min(character.getMaxMp(), character.getMp() + restoredMana));
    }
}
