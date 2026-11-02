package com.prj.habittracker.rpg.skill.effect.penalty;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.common.enums.SkillTargetType;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProtectStreakEffectHandler implements PenaltySkillEffectHandler {
    @Override
    public SkillEffectType supports() {
        return SkillEffectType.PROTECT_STREAK;
    }

    @Override
    public boolean isApplicable(SkillCastEntity cast, PenaltyEffectContext context) {
        return cast.getTargetType() == SkillTargetType.HABIT
                && cast.getTargetId() != null
                && context.missedHabitIds().contains(cast.getTargetId());
    }

    @Override
    public PenaltyAdjustment apply(SkillEffectEntity effect, SkillCastEntity cast, PenaltyAdjustment adjustment) {
        return adjustment.protect(cast.getTargetId());
    }
}
