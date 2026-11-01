package com.prj.habittracker.rpg.skill.service.impl;

import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.common.enums.SkillCastStatus;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.skill.effect.completion.CompletionEffectContext;
import com.prj.habittracker.rpg.skill.effect.completion.CompletionSkillEffectRegistry;
import com.prj.habittracker.rpg.skill.effect.penalty.PenaltySkillEffectRegistry;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.repository.SkillCastRepository;
import com.prj.habittracker.rpg.skill.service.SkillEffectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkillEffectServiceImpl implements SkillEffectService {
    private final SkillCastRepository skillCastRepository;
    private final CompletionSkillEffectRegistry completionRegistry;
    private final PenaltySkillEffectRegistry penaltyRegistry;

    @Override
    @Transactional
    public RpgReward applyCompletionEffects(UUID userId, RewardSourceType sourceType, UUID sourceId, RpgReward baseReward) {
        OffsetDateTime now = OffsetDateTime.now();
        RpgReward reward = baseReward;
        CompletionEffectContext context = new CompletionEffectContext(sourceType, sourceId);

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
        return reward;
    }

    @Override
    @Transactional
    public int applyPenaltyEffects(UUID userId, int rawDamage) {
        if (rawDamage <= 0) return 0;
        OffsetDateTime now = OffsetDateTime.now();
        int damage = rawDamage;
        for (SkillCastEntity cast : skillCastRepository.findAllActive(userId, now)) {
            boolean applied = false;
            for (var effect : cast.getSkill().getEffects()) {
                var handler = penaltyRegistry.find(effect.getEffectType()).orElse(null);
                if (handler != null) {
                    damage = handler.apply(effect, damage);
                    applied = true;
                }
            }
            if (applied) consume(cast, now);
            if (damage == 0) break;
        }
        return damage;
    }

    private void consume(SkillCastEntity cast, OffsetDateTime now) {
        cast.setStatus(SkillCastStatus.CONSUMED);
        cast.setConsumedAt(now);
    }
}
