package com.prj.habittracker.rpg.skill.service.impl;

import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.rpg.character.service.ManaService;
import com.prj.habittracker.rpg.skill.effect.SkillEffectActivationService;
import com.prj.habittracker.rpg.skill.policy.SkillCastPolicy;
import com.prj.habittracker.rpg.skill.policy.target.SkillTargetPolicy;
import com.prj.habittracker.rpg.skill.dto.request.CastSkillRequest;
import com.prj.habittracker.rpg.skill.dto.response.SkillCastResponse;
import com.prj.habittracker.rpg.skill.dto.response.SkillEffectResponse;
import com.prj.habittracker.rpg.skill.dto.response.SkillResponse;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEntity;
import com.prj.habittracker.rpg.skill.repository.SkillCastRepository;
import com.prj.habittracker.rpg.skill.repository.SkillRepository;
import com.prj.habittracker.rpg.skill.service.SkillService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillCastRepository skillCastRepository;
    private final PlayerCharacterRepository playerCharacterRepository;
    private final CurrentUserService currentUserService;
    private final ManaService manaService;
    private final SkillCastPolicy skillCastPolicy;
    private final SkillTargetPolicy skillTargetPolicy;
    private final SkillEffectActivationService activationService;

    @Override
    @Transactional
    public List<SkillResponse> getSkills() {
        UUID userId = currentUserService.getCurrentUserId();
        PlayerCharacterEntity character = playerCharacterRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found"));
        manaService.regenerate(character, LocalDate.now());
        if (character.getCharacterClass() == null) {
            return List.of();
        }
        OffsetDateTime now = OffsetDateTime.now();
        return skillRepository.findAllByCharacterClassAndEnabledTrue(character.getCharacterClass()).stream()
                .map(skill -> toResponse(skill, character, userId, now))
                .toList();
    }

    @Override
    @Transactional
    public SkillCastResponse cast(UUID skillId, CastSkillRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        PlayerCharacterEntity character = playerCharacterRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found"));
        manaService.regenerate(character, LocalDate.now());

        SkillCastEntity duplicate = skillCastRepository
                .findByUserIdAndIdempotencyKey(userId, request.idempotencyKey())
                .orElse(null);
        if (duplicate != null) {
            return toCastResponse(duplicate, character);
        }

        SkillEntity skill = skillRepository.findByIdAndEnabledTrue(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found"));
        OffsetDateTime now = OffsetDateTime.now();
        skillCastPolicy.validate(userId, character, skill, now);
        skillTargetPolicy.validate(skill.getTargetType(), userId, request.targetId());
        manaService.spend(character, skill.getManaCost());

        SkillCastEntity cast = createCast(userId, character, skill, request, now);
        activationService.activate(cast, character, now);

        return toCastResponse(skillCastRepository.save(cast), character);
    }

    private SkillCastEntity createCast(UUID userId, PlayerCharacterEntity character, SkillEntity skill,
                                       CastSkillRequest request, OffsetDateTime now) {
        SkillCastEntity cast = new SkillCastEntity();
        cast.setUserId(userId);
        cast.setCharacter(character);
        cast.setSkill(skill);
        cast.setTargetType(skill.getTargetType());
        cast.setTargetId(request.targetId());
        cast.setManaSpent(skill.getManaCost());
        cast.setCastAt(now);
        cast.setIdempotencyKey(request.idempotencyKey());
        return cast;
    }

    private SkillResponse toResponse(SkillEntity skill, PlayerCharacterEntity character, UUID userId, OffsetDateTime now) {
        SkillCastEntity previous = skillCastRepository
                .findFirstByUserIdAndSkillIdOrderByCastAtDesc(userId, skill.getId())
                .orElse(null);
        Long remainingCooldown = null;
        if (previous != null) {
            long seconds = Duration.between(now, previous.getCastAt().plusSeconds(skill.getCooldownSeconds())).getSeconds();
            remainingCooldown = Math.max(0, seconds);
        }
        boolean active = skillCastRepository.existsActive(userId, skill.getId(), now);
        return new SkillResponse(
                skill.getId(), skill.getCode(), skill.getName(), skill.getDescription(),
                skill.getCharacterClass(), skill.getSkillType(), skill.getManaCost(), skill.getRequiredLevel(),
                skill.getCooldownSeconds(), skill.getTargetType(), character.getLevel() >= skill.getRequiredLevel(),
                active, remainingCooldown,
                skill.getEffects().stream().map(effect -> new SkillEffectResponse(
                        effect.getEffectType(), effect.getValue(), effect.getConditionValue(), effect.getValueType(), effect.getDurationType()
                )).toList()
        );
    }

    private SkillCastResponse toCastResponse(SkillCastEntity cast, PlayerCharacterEntity character) {
        return new SkillCastResponse(
                cast.getId(), cast.getSkill().getCode(), cast.getStatus(), cast.getManaSpent(),
                character.getMp(), character.getHp(), character.getCoin(), cast.getExpiresAt()
        );
    }
}
