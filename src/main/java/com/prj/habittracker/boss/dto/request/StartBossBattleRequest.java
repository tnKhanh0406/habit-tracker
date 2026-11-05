package com.prj.habittracker.boss.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartBossBattleRequest(@NotNull UUID bossDefinitionId) {}
