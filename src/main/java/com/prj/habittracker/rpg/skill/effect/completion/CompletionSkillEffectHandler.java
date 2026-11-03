package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;

public interface CompletionSkillEffectHandler {
    SkillEffectType supports();
    boolean isApplicable(SkillCastEntity cast, CompletionEffectContext context);
    RpgReward apply(SkillEffectEntity effect, RpgReward reward);

    default RpgReward apply(SkillEffectEntity effect, SkillCastEntity cast, RpgReward reward) {
        return apply(effect, reward);
    }
}
