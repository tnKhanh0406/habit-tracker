package com.prj.habittracker.rpg.skill.effect.penalty;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;

public interface PenaltySkillEffectHandler {
    SkillEffectType supports();
    int apply(SkillEffectEntity effect, int damage);
}
