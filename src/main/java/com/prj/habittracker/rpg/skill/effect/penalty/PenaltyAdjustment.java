package com.prj.habittracker.rpg.skill.effect.penalty;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record PenaltyAdjustment(int damage, Set<UUID> protectedHabitIds) {
    public PenaltyAdjustment {
        protectedHabitIds = Set.copyOf(protectedHabitIds);
    }

    public PenaltyAdjustment withDamage(int newDamage) {
        return new PenaltyAdjustment(newDamage, protectedHabitIds);
    }

    public PenaltyAdjustment protect(UUID habitId) {
        Set<UUID> protectedIds = new HashSet<>(protectedHabitIds);
        protectedIds.add(habitId);
        return new PenaltyAdjustment(damage, protectedIds);
    }
}
