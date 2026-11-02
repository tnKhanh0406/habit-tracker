package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.common.enums.SkillTargetType;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;

final class CompletionTargetMatcher {
    private CompletionTargetMatcher() {}

    static boolean matches(SkillCastEntity cast, CompletionEffectContext context) {
        if (cast.getTargetType() == SkillTargetType.NEXT_COMPLETION) {
            return true;
        }
        if (cast.getTargetType() == SkillTargetType.TODO) {
            return context.sourceType() == RewardSourceType.TODO_COMPLETION
                    && context.sourceId().equals(cast.getTargetId());
        }
        return false;
    }
}
