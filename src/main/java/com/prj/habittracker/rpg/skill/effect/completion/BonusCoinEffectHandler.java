package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.springframework.stereotype.Component;

@Component
public class BonusCoinEffectHandler implements CompletionSkillEffectHandler {
    @Override
    public SkillEffectType supports() { return SkillEffectType.BONUS_COIN; }

    @Override
    public boolean isApplicable(SkillCastEntity cast, CompletionEffectContext context) {
        return context.sourceType() == RewardSourceType.TODO_COMPLETION
                && context.sourceId().equals(cast.getTargetId());
    }

    @Override
    public RpgReward apply(SkillEffectEntity effect, RpgReward reward) {
        return new RpgReward(
                reward.exp(),
                reward.coin() + PercentRewardBonus.calculate(reward.coin(), effect.getValue())
        );
    }
}
