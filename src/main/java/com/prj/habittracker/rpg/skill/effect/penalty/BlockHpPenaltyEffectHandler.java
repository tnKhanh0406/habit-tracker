package com.prj.habittracker.rpg.skill.effect.penalty;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.common.enums.SkillTargetType;
import org.springframework.stereotype.Component;

@Component
public class BlockHpPenaltyEffectHandler implements PenaltySkillEffectHandler {
    @Override
    public SkillEffectType supports() { return SkillEffectType.BLOCK_HP_PENALTY; }

    @Override
    public boolean isApplicable(SkillCastEntity cast, PenaltyEffectContext context) {
        return context.rawDamage() > 0 && (cast.getTargetType() == SkillTargetType.SELF
                || (cast.getTargetType() == SkillTargetType.HABIT
                && context.missedHabitIds().contains(cast.getTargetId())));
    }

    @Override
    public PenaltyAdjustment apply(SkillEffectEntity effect, SkillCastEntity cast, PenaltyAdjustment adjustment) {
        return adjustment.withDamage(Math.max(0, adjustment.damage() - effect.getValue().intValue()));
    }
}
