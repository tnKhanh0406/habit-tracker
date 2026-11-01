package com.prj.habittracker.rpg.skill.service;

import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;

import java.util.UUID;

public interface SkillEffectService {
    RpgReward applyCompletionEffects(UUID userId, RewardSourceType sourceType, UUID sourceId, RpgReward baseReward);
    int applyPenaltyEffects(UUID userId, int rawDamage);
}
