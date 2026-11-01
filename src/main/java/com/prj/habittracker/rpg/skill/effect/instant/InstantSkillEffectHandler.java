package com.prj.habittracker.rpg.skill.effect.instant;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;

public interface InstantSkillEffectHandler {
    SkillEffectType supports();
    void apply(SkillEffectEntity effect, PlayerCharacterEntity character);
}
