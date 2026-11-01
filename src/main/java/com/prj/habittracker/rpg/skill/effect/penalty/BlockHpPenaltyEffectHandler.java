package com.prj.habittracker.rpg.skill.effect.penalty;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.springframework.stereotype.Component;

@Component
public class BlockHpPenaltyEffectHandler implements PenaltySkillEffectHandler {
    @Override
    public SkillEffectType supports() { return SkillEffectType.BLOCK_HP_PENALTY; }

    @Override
    public int apply(SkillEffectEntity effect, int damage) {
        return Math.max(0, damage - effect.getValue().intValue());
    }
}
