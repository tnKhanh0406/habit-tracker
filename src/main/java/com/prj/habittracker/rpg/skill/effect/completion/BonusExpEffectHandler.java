package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.springframework.stereotype.Component;

@Component
public class BonusExpEffectHandler implements CompletionSkillEffectHandler {
    @Override
    public SkillEffectType supports() { return SkillEffectType.BONUS_EXP; }

    @Override
    public boolean isApplicable(SkillCastEntity cast, CompletionEffectContext context) { return true; }

    @Override
    public RpgReward apply(SkillEffectEntity effect, RpgReward reward) {
        return new RpgReward(
                reward.exp() + PercentRewardBonus.calculate(reward.exp(), effect.getValue()),
                reward.coin()
        );
    }
}
