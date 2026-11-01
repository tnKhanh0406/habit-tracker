package com.prj.habittracker.rpg.skill.policy;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEntity;
import com.prj.habittracker.rpg.skill.repository.SkillCastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SkillCastPolicy {
    private final SkillCastRepository skillCastRepository;

    public void validate(UUID userId, PlayerCharacterEntity character, SkillEntity skill, OffsetDateTime now) {
        if (character.getCharacterClass() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Select a character class first");
        }
        if (skill.getCharacterClass() != character.getCharacterClass()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Skill does not belong to this character class");
        }
        if (character.getLevel() < skill.getRequiredLevel()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Character level is too low");
        }

        SkillCastEntity previous = skillCastRepository
                .findFirstByUserIdAndSkillIdOrderByCastAtDesc(userId, skill.getId())
                .orElse(null);
        if (previous != null && previous.getCastAt().plusSeconds(skill.getCooldownSeconds()).isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Skill is on cooldown");
        }
        if (skillCastRepository.existsActive(userId, skill.getId(), now)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This skill already has an active effect");
        }
    }
}
