package com.prj.habittracker.rpg.character.service.impl;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.service.ManaService;
import com.prj.habittracker.rpg.character.service.CharacterStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ManaServiceImpl implements ManaService {
    private static final int MANA_PER_DAY = 10;
    private final CharacterStatService characterStatService;

    @Override
    public void regenerate(PlayerCharacterEntity character, LocalDate today) {
        LocalDate lastDate = character.getManaRegeneratedAt();
        if (lastDate == null) {
            character.setManaRegeneratedAt(today);
            return;
        }
        long days = ChronoUnit.DAYS.between(lastDate, today);
        if (days <= 0) return;

        long restored = Math.min((long) characterStatService.calculate(character).maxMp(),
                character.getMp() + days * MANA_PER_DAY);
        character.setMp((int) restored);
        character.setManaRegeneratedAt(today);
    }

    @Override
    public void spend(PlayerCharacterEntity character, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Mana cost cannot be negative");
        }
        if (character.getMp() < amount) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough mana");
        }
        character.setMp(character.getMp() - amount);
    }
}
