package com.prj.habittracker.rpg.character.service.impl;

import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.rpg.character.config.LevelPolicy;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.rpg.character.service.CharacterProgressionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CharacterProgressionServiceImpl implements CharacterProgressionService {
    private final PlayerCharacterRepository playerCharacterRepository;
    private final CurrentUserService currentUserService;
    private final LevelPolicy levelPolicy;

    private PlayerCharacterEntity getPlayerCharacter(UUID userId) {
        return playerCharacterRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Player character not found for user"));
    }

    @Override
    public CharacterResponse applyReward(UUID userId, long exp, long coin) {
        if (exp < 0 || coin < 0) {
            throw new IllegalArgumentException("Reward cannot be negative");
        }

        PlayerCharacterEntity character = getPlayerCharacter(userId);
        character.setCoin(character.getCoin() + coin);
        addExp(character, exp);

        return CharacterResponse.from(character, levelPolicy.requiredExp(character.getLevel()));
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

        return CharacterResponse.from(character, levelPolicy.requiredExp(character.getLevel()));
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
            character.setHp(character.getMaxHp());
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
