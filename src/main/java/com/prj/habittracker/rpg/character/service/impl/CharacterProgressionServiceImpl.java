package com.prj.habittracker.rpg.character.service.impl;

import com.prj.habittracker.rpg.character.config.LevelPolicy;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.rpg.character.service.CharacterProgressionService;
import com.prj.habittracker.rpg.character.service.CharacterResponseFactory;
import com.prj.habittracker.rpg.character.service.CharacterStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CharacterProgressionServiceImpl implements CharacterProgressionService {
    private final PlayerCharacterRepository playerCharacterRepository;
    private final LevelPolicy levelPolicy;
    private final CharacterResponseFactory characterResponseFactory;
    private final CharacterStatService characterStatService;

    private PlayerCharacterEntity getPlayerCharacter(UUID userId) {
        return playerCharacterRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Player character not found for user"));
    }

    @Override
    public CharacterResponse getCharacter(UUID userId) {
        PlayerCharacterEntity character = getPlayerCharacter(userId);
        return characterResponseFactory.create(character);
    }

    @Override
    public CharacterResponse applyHpDamage(UUID userId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("HP damage must be greater than 0");
        }
        PlayerCharacterEntity character = getPlayerCharacter(userId);
        if (amount >= character.getHp()) {
            character.setCoin(character.getCoin() - Math.min(50L, character.getCoin() / 10));
            character.setHp(Math.max(1, characterStatService.calculate(character).maxHp() / 2));
        } else {
            character.setHp(character.getHp() - amount);
        }
        return characterResponseFactory.create(character);
    }

    @Override
    public CharacterResponse applyReward(UUID userId, long exp, long coin) {
        if (exp < 0 || coin < 0) {
            throw new IllegalArgumentException("Reward cannot be negative");
        }

        PlayerCharacterEntity character = getPlayerCharacter(userId);
        character.setCoin(character.getCoin() + coin);
        addExp(character, exp);

        return characterResponseFactory.create(character);
    }

    @Override
    public CharacterResponse reverseReward(UUID userId, long exp, long coin) {
        if (exp < 0 || coin < 0) {
            throw new IllegalArgumentException("Reward cannot be negative");
        }

        PlayerCharacterEntity character = getPlayerCharacter(userId);

        if (character.getCoin() < coin) {
            character.setCoin(0L);
        } else {
            character.setCoin(character.getCoin() - coin);
        }

        removeExp(character, exp);

        return characterResponseFactory.create(character);
    }

    private void addExp(PlayerCharacterEntity character, long amount) {
        if (amount <= 0) {
            return;
        }

        boolean reachedNewLevel = false;

        character.setExp(character.getExp() + amount);

        while (true) {
            long required = levelPolicy.requiredExp(character.getLevel());

            if (character.getExp() < required) {
                break;
            }

            character.setExp(character.getExp() - required);
            character.setLevel(character.getLevel() + 1);

            if (character.getLevel() > character.getMaxLevelReached()) {
                character.setMaxLevelReached(character.getLevel());
                character.setStatPoints(character.getStatPoints() + 1);
                reachedNewLevel = true;
            }
        }

        if (reachedNewLevel) {
            character.setHp(characterStatService.calculate(character).maxHp());
        }
    }

    private void removeExp(PlayerCharacterEntity character, long amount) {
        if (amount <= 0) {
            return;
        }

        long remaining = amount;

        while (remaining > 0) {
            long currentExp = character.getExp();
            if (currentExp >= remaining) {
                character.setExp(currentExp - remaining);
                return;
            }

            remaining -= currentExp;

            if (character.getLevel() <= 1) {
                character.setExp(0L);
                return;
            }

            int previousLevel = character.getLevel() - 1;
            long previousLevelRequiredExp = levelPolicy.requiredExp(previousLevel);

            character.setLevel(previousLevel);

            if (remaining <= previousLevelRequiredExp) {
                character.setExp(previousLevelRequiredExp - remaining);
                return;
            }

            remaining -= previousLevelRequiredExp;
            character.setExp(0L);
        }
    }
}
