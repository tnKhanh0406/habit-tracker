package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.springframework.stereotype.Component;

@Component
public class HealHpAfterCompletionEffectHandler implements CompletionSkillEffectHandler {
    public SkillEffectType supports() { return SkillEffectType.HEAL_HP_AFTER_COMPLETION; }

    public boolean isApplicable(SkillCastEntity cast, CompletionEffectContext context) {
        return CompletionTargetMatcher.matches(cast, context);
    }

    public RpgReward apply(SkillEffectEntity effect, RpgReward reward) {
        throw new IllegalStateException("Recovery requires a cast character");
    }

    @Override
    public RpgReward apply(SkillEffectEntity effect, SkillCastEntity cast, RpgReward reward) {
        var character = cast.getCharacter();
        character.setHp(Math.min(character.getMaxHp(), character.getHp() + effect.getValue().intValue()));
        return reward;
    }
}
