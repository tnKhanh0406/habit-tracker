package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.springframework.stereotype.Component;

@Component
public class EarlyCompletionBonusCoinEffectHandler implements CompletionSkillEffectHandler {
    @Override
    public SkillEffectType supports() {
        return SkillEffectType.EARLY_COMPLETION_BONUS_COIN;
    }

    @Override
    public boolean isApplicable(SkillCastEntity cast, CompletionEffectContext context) {
        return context.completedBeforeDeadline() && CompletionTargetMatcher.matches(cast, context);
    }

    @Override
    public RpgReward apply(SkillEffectEntity effect, RpgReward reward) {
        return new RpgReward(
                reward.exp(), reward.coin() + PercentRewardBonus.calculate(reward.coin(), effect.getValue()));
    }
}
