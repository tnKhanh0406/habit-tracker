package com.prj.habittracker.rpg.character.service.impl;

import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.reward.entity.RewardEntity;
import com.prj.habittracker.reward.repository.RewardRepository;
import com.prj.habittracker.rpg.character.config.LevelPolicy;
import com.prj.habittracker.rpg.character.dto.request.AllocateStatRequest;
import com.prj.habittracker.rpg.character.dto.request.SelectCharacterClassRequest;
import com.prj.habittracker.rpg.character.dto.response.AllocateStatResponse;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.rpg.character.service.PlayerCharacterService;
import com.prj.habittracker.inventory.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerCharacterServiceImpl implements PlayerCharacterService {
    private final PlayerCharacterRepository playerCharacterRepository;
    private final CurrentUserService currentUserService;
    private final LevelPolicy levelPolicy;
    private final RewardRepository rewardRepository;
    private final com.prj.habittracker.rpg.skill.repository.SkillCastRepository skillCastRepository;
    private final InventoryService inventoryService;

    @Override
    public CharacterResponse getCharacter() {
        UUID userId = currentUserService.getCurrentUserId();
        PlayerCharacterEntity playerCharacter = playerCharacterRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found for user ID: " + userId));
        return CharacterResponse.from(playerCharacter, levelPolicy.requiredExp(playerCharacter.getLevel()));
    }

    @Override
    @Transactional
    public CharacterResponse createCharacter(UUID userId) {
        if (playerCharacterRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Character already exists for user: " + userId);
        }

        PlayerCharacterEntity character = PlayerCharacterEntity.builder()
                .userId(userId)
                .maxLevelReached(1)
                .level(1)
                .exp(0L)

                .hp(100)
                .maxHp(100)

                .mp(50)
                .maxMp(50)

                .coin(0L)
                .gem(0L)

                .strength(1)
                .intelligence(1)
                .vitality(1)
                .agility(1)

                .statPoints(0)
                .manaRegeneratedAt(LocalDate.now())
                .build();

        PlayerCharacterEntity saved = playerCharacterRepository.save(character);
        return CharacterResponse.from(saved, levelPolicy.requiredExp(saved.getLevel()));
    }

    @Override
    @Transactional
    public AllocateStatResponse allocateStat(AllocateStatRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        PlayerCharacterEntity character = playerCharacterRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found for user ID: " + userId));
        if (character.getStatPoints() < request.points()) {
            throw new IllegalStateException("Not enough stat points");
        }

        switch (request.stat()) {
            case STRENGTH -> character.setStrength(character.getStrength() + request.points());

            case INTELLIGENCE -> character.setIntelligence(character.getIntelligence() + request.points());

            case VITALITY -> character.setVitality(character.getVitality() + request.points());

            case AGILITY -> character.setAgility(character.getAgility() + request.points());
        }

        character.setStatPoints(character.getStatPoints() - request.points());
        PlayerCharacterEntity saved = playerCharacterRepository.save(character);
        return new AllocateStatResponse(
                saved.getStrength(),
                saved.getIntelligence(),
                saved.getVitality(),
                saved.getAgility(),
                saved.getStatPoints()
        );
    }

    @Override
    @Transactional
    public long redeemReward(UUID rewardId) {
        UUID userId = currentUserService.getCurrentUserId();
        RewardEntity reward = rewardRepository.findByIdAndUserId(rewardId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Reward not found: " + rewardId));
        PlayerCharacterEntity character = playerCharacterRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found for user ID: " + userId));
        if (character.getCoin() < reward.getCost()) {
            throw new IllegalStateException("Not enough coins to redeem reward");
        }
        long remainingCoins = character.getCoin() - reward.getCost();
        character.setCoin(remainingCoins);
        playerCharacterRepository.save(character);
        return remainingCoins;
    }

    @Override
    @Transactional
    public CharacterResponse selectClass(SelectCharacterClassRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        PlayerCharacterEntity character = playerCharacterRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found for user ID: " + userId));
        var now = java.time.OffsetDateTime.now();
        boolean firstClassSelection = character.getCharacterClass() == null;
        if (character.getCharacterClass() == request.characterClass()) {
            return CharacterResponse.from(character, levelPolicy.requiredExp(character.getLevel()));
        }
        if (character.getClassChangedAt() != null && character.getClassChangedAt().plusDays(7).isAfter(now)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,
                    "You can change class again at " + character.getClassChangedAt().plusDays(7));
        }
        skillCastRepository.findAllActive(userId, now).forEach(cast -> {
            cast.setStatus(com.prj.habittracker.common.enums.SkillCastStatus.CANCELLED);
        });
        inventoryService.unequipIncompatibleItems(userId, request.characterClass());
        character.setCharacterClass(request.characterClass());
        character.setClassChangedAt(now);
        if (firstClassSelection) {
            inventoryService.grantStarterPack(userId, request.characterClass());
        }
        return CharacterResponse.from(character, levelPolicy.requiredExp(character.getLevel()));
    }


}
