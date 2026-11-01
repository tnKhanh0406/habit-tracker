package com.prj.habittracker.rpg.skill.policy.target;

import com.prj.habittracker.common.enums.SkillTargetType;

import java.util.UUID;

public interface SkillTargetValidator {
    boolean supports(SkillTargetType targetType);
    void validate(UUID userId, UUID targetId);
}
