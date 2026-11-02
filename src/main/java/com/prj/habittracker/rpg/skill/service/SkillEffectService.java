package com.prj.habittracker.rpg.skill.service;

import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;

import java.util.UUID;
import java.util.Set;
import com.prj.habittracker.rpg.skill.effect.penalty.PenaltyAdjustment;

public interface SkillEffectService {
    RpgReward applyCompletionEffects(UUID userId, RewardSourceType sourceType, UUID sourceId, int streak, RpgReward baseReward);
    PenaltyAdjustment applyPenaltyEffects(UUID userId, int rawDamage, Set<UUID> missedHabitIds);
}
