package com.prj.habittracker.rpg.character.dto.request;

import com.prj.habittracker.common.enums.CharacterStat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AllocateStatRequest(
        @NotNull
        CharacterStat stat,

        @Min(value = 1, message = "Points must be at least 1")
        int points
) {
}
