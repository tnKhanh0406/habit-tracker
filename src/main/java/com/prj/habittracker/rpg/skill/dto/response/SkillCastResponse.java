package com.prj.habittracker.rpg.skill.dto.response;

import com.prj.habittracker.common.enums.SkillCastStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SkillCastResponse(
        UUID castId,
        String skillCode,
        SkillCastStatus status,
        int manaSpent,
        int currentMana,
        int currentHp,
        OffsetDateTime expiresAt
) {}
