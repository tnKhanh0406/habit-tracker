package com.prj.habittracker.rpg.character.dto.request;

import com.prj.habittracker.common.enums.CharacterClass;
import jakarta.validation.constraints.NotNull;

public record SelectCharacterClassRequest(@NotNull CharacterClass characterClass) {}
