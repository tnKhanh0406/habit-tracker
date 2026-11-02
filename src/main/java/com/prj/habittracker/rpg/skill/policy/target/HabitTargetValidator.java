package com.prj.habittracker.rpg.skill.policy.target;

import com.prj.habittracker.common.enums.SkillTargetType;
import com.prj.habittracker.habit.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HabitTargetValidator implements SkillTargetValidator {
    private final HabitRepository habitRepository;

    @Override
    public boolean supports(SkillTargetType targetType) {
        return targetType == SkillTargetType.HABIT;
    }

    @Override
    public void validate(UUID userId, UUID targetId) {
        if (targetId == null || !habitRepository.existsByIdAndUserId(targetId, userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit target not found");
        }
    }
}
