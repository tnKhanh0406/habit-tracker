package com.prj.habittracker.rpg.skill.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CastSkillRequest(
        UUID targetId,
        @NotNull UUID idempotencyKey
) {}
