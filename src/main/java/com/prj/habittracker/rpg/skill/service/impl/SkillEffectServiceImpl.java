package com.prj.habittracker.rpg.skill.service.impl;

import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.common.enums.SkillCastStatus;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.skill.effect.completion.CompletionEffectContext;
import com.prj.habittracker.rpg.skill.effect.completion.CompletionSkillEffectRegistry;
import com.prj.habittracker.rpg.skill.effect.completion.PassiveCompletionEffectService;
import com.prj.habittracker.rpg.skill.effect.penalty.PenaltySkillEffectRegistry;
import com.prj.habittracker.rpg.skill.effect.penalty.PenaltyAdjustment;
import com.prj.habittracker.rpg.skill.effect.penalty.PenaltyEffectContext;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.repository.SkillCastRepository;
import com.prj.habittracker.rpg.skill.service.SkillEffectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SkillEffectServiceImpl implements SkillEffectService {
    private final SkillCastRepository skillCastRepository;
    private final CompletionSkillEffectRegistry completionRegistry;
    private final PenaltySkillEffectRegistry penaltyRegistry;
    private final PassiveCompletionEffectService passiveCompletionEffectService;

    @Override
    @Transactional
    public RpgReward applyCompletionEffects(UUID userId, RewardSourceType sourceType, UUID sourceId, int streak, RpgReward baseReward) {
        OffsetDateTime now = OffsetDateTime.now();
        RpgReward reward = baseReward;
        CompletionEffectContext context = new CompletionEffectContext(sourceType, sourceId, streak);

        for (SkillCastEntity cast : skillCastRepository.findAllActive(userId, now)) {
            boolean applied = false;
            for (var effect : cast.getSkill().getEffects()) {
                var handler = completionRegistry.find(effect.getEffectType()).orElse(null);
                if (handler != null && handler.isApplicable(cast, context)) {
                    reward = handler.apply(effect, reward);
                    applied = true;
                }
            }
            if (applied) consume(cast, now);
        }
        return passiveCompletionEffectService.apply(userId, context, reward);
    }

    @Override
    @Transactional
    public PenaltyAdjustment applyPenaltyEffects(UUID userId, int rawDamage, Set<UUID> missedHabitIds) {
        PenaltyAdjustment adjustment = new PenaltyAdjustment(Math.max(0, rawDamage), Set.of());
        if (rawDamage <= 0) return adjustment;
        OffsetDateTime now = OffsetDateTime.now();
        PenaltyEffectContext context = new PenaltyEffectContext(rawDamage, missedHabitIds);
        for (SkillCastEntity cast : skillCastRepository.findAllActive(userId, now)) {
            boolean applied = false;
            for (var effect : cast.getSkill().getEffects()) {
                var handler = penaltyRegistry.find(effect.getEffectType()).orElse(null);
                if (handler != null && handler.isApplicable(cast, context)) {
                    adjustment = handler.apply(effect, cast, adjustment);
                    applied = true;
                }
            }
            if (applied) consume(cast, now);
        }
        return adjustment;
    }

    private void consume(SkillCastEntity cast, OffsetDateTime now) {
        cast.setStatus(SkillCastStatus.CONSUMED);
        cast.setConsumedAt(now);
    }
}
