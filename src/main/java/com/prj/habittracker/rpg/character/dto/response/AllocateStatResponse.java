package com.prj.habittracker.rpg.character.dto.response;

public record AllocateStatResponse(
    int strength,
    int intelligence,
    int vitality,
    int agility,
    int statPoints
) {
}
