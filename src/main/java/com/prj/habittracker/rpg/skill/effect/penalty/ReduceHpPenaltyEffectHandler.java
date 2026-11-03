package com.prj.habittracker.rpg.skill.effect.penalty;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.common.enums.SkillTargetType;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class ReduceHpPenaltyEffectHandler implements PenaltySkillEffectHandler {
    public SkillEffectType supports() { return SkillEffectType.REDUCE_HP_PENALTY; }

    public boolean isApplicable(SkillCastEntity cast, PenaltyEffectContext context) {
        return cast.getTargetType() == SkillTargetType.SELF && context.rawDamage() > 0;
    }

    public PenaltyAdjustment apply(SkillEffectEntity effect, SkillCastEntity cast, PenaltyAdjustment adjustment) {
        BigDecimal percent = effect.getValue().max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));
        int damage = BigDecimal.valueOf(adjustment.damage())
                .multiply(BigDecimal.valueOf(100).subtract(percent))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.CEILING).intValueExact();
        return adjustment.withDamage(damage);
    }
}
