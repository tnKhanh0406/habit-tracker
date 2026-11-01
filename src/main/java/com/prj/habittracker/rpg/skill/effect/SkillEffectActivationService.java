package com.prj.habittracker.rpg.skill.effect;

import com.prj.habittracker.common.enums.SkillCastStatus;
import com.prj.habittracker.common.enums.SkillDurationType;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.skill.effect.instant.InstantSkillEffectRegistry;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class SkillEffectActivationService {
    private static final int ACTIVE_EFFECT_HOURS = 24;
    private final InstantSkillEffectRegistry instantRegistry;

    public void activate(SkillCastEntity cast, PlayerCharacterEntity character, OffsetDateTime now) {
        boolean hasDeferredEffect = false;
        for (var effect : cast.getSkill().getEffects()) {
            if (effect.getDurationType() == SkillDurationType.INSTANT) {
                instantRegistry.get(effect.getEffectType()).apply(effect, character);
            } else {
                hasDeferredEffect = true;
            }
        }

        if (hasDeferredEffect) {
            cast.setStatus(SkillCastStatus.ACTIVE);
            cast.setExpiresAt(now.plusHours(ACTIVE_EFFECT_HOURS));
        } else {
            cast.setStatus(SkillCastStatus.CONSUMED);
            cast.setConsumedAt(now);
        }
    }
}
