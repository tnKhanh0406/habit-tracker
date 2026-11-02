package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.RewardSourceType;

import java.util.UUID;

public record CompletionEffectContext(RewardSourceType sourceType, UUID sourceId, int streak) {}
