package com.prj.habittracker.rpg.skill.policy.target;

import com.prj.habittracker.common.enums.SkillTargetType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SkillTargetPolicy {
    private final List<SkillTargetValidator> validators;

    public SkillTargetPolicy(List<SkillTargetValidator> validators) {
        this.validators = List.copyOf(validators);
    }

    public void validate(SkillTargetType targetType, UUID userId, UUID targetId) {
        validators.stream()
                .filter(validator -> validator.supports(targetType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No target validator for " + targetType))
                .validate(userId, targetId);
    }
}
