package com.prj.habittracker.rpg.skill.dto.response;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.common.enums.SkillTargetType;
import com.prj.habittracker.common.enums.SkillType;

import java.util.List;
import java.util.UUID;

public record SkillResponse(
        UUID id,
        String code,
        String name,
        String description,
        CharacterClass characterClass,
        SkillType skillType,
        int manaCost,
        int requiredLevel,
        int cooldownSeconds,
        SkillTargetType targetType,
        boolean unlocked,
        boolean active,
        Long remainingCooldownSeconds,
        List<SkillEffectResponse> effects
) {}
