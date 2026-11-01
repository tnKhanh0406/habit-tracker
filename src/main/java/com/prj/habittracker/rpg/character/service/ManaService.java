package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;

import java.time.LocalDate;

public interface ManaService {
    void regenerate(PlayerCharacterEntity character, LocalDate today);
    void spend(PlayerCharacterEntity character, int amount);
}
