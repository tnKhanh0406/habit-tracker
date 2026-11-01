package com.prj.habittracker.rpg.skill.policy.target;

import com.prj.habittracker.common.enums.SkillTargetType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class NoTargetValidator implements SkillTargetValidator {
    @Override
    public boolean supports(SkillTargetType targetType) {
        return targetType == SkillTargetType.SELF || targetType == SkillTargetType.NEXT_COMPLETION;
    }

    @Override
    public void validate(UUID userId, UUID targetId) {
        if (targetId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This skill does not accept a target");
        }
    }
}
