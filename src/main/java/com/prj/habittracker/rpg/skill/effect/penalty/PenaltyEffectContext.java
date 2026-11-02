package com.prj.habittracker.rpg.skill.effect.penalty;

import java.util.Set;
import java.util.UUID;

public record PenaltyEffectContext(int rawDamage, Set<UUID> missedHabitIds) {
    public PenaltyEffectContext {
        missedHabitIds = Set.copyOf(missedHabitIds);
    }
}
