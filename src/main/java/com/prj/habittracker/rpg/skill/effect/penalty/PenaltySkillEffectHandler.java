package com.prj.habittracker.rpg.skill.effect.penalty;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;

public interface PenaltySkillEffectHandler {
    SkillEffectType supports();
    boolean isApplicable(SkillCastEntity cast, PenaltyEffectContext context);
    PenaltyAdjustment apply(SkillEffectEntity effect, SkillCastEntity cast, PenaltyAdjustment adjustment);
}
