package com.prj.habittracker.rpg.skill.policy.target;

import com.prj.habittracker.common.enums.SkillTargetType;
import com.prj.habittracker.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TodoTargetValidator implements SkillTargetValidator {
    private final TodoRepository todoRepository;

    @Override
    public boolean supports(SkillTargetType targetType) {
        return targetType == SkillTargetType.TODO;
    }

    @Override
    public void validate(UUID userId, UUID targetId) {
        if (targetId == null || !todoRepository.existsByIdAndUserId(targetId, userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A todo owned by the current user is required");
        }
    }
}
