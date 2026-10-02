package com.prj.habittracker.rpg.character.config;

import org.springframework.stereotype.Component;

@Component
public class LevelPolicy {
    public long requiredExp(int currentLevel) {
        if (currentLevel < 1) {
            throw new IllegalArgumentException("Level must be >= 1");
        }
        return 100L + (currentLevel - 1L) * 50L;
    }
}
