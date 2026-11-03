package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.common.enums.SkillType;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import com.prj.habittracker.rpg.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassiveCompletionEffectService {
    private final PlayerCharacterRepository playerCharacterRepository;
    private final SkillRepository skillRepository;

    public RpgReward apply(UUID userId, CompletionEffectContext context, RpgReward reward) {
        var character = playerCharacterRepository.findByUserId(userId).orElse(null);
        if (character == null || character.getCharacterClass() == null) {
            return reward;
        }

        var effects = skillRepository.findAllByCharacterClassAndSkillTypeAndEnabledTrue(
                        character.getCharacterClass(), SkillType.PASSIVE).stream()
                .flatMap(skill -> skill.getEffects().stream())
                .toList();

        BigDecimal expBonusPercent = effects.stream()
                .filter(effect -> effect.getEffectType() == SkillEffectType.STREAK_BONUS_EXP)
                .filter(effect -> context.sourceType() == RewardSourceType.HABIT_COMPLETION)
                .filter(effect -> effect.getConditionValue() != null
                        && context.streak() >= effect.getConditionValue().intValue())
                .max(Comparator.comparing(SkillEffectEntity::getConditionValue))
                .map(SkillEffectEntity::getValue)
                .orElse(BigDecimal.ZERO);

        if (context.sourceType() == RewardSourceType.HABIT_COMPLETION
                && (context.difficulty() == Difficulty.HARD || context.difficulty() == Difficulty.EXTREME)) {
            expBonusPercent = expBonusPercent.add(effects.stream()
                    .filter(effect -> effect.getEffectType() == SkillEffectType.DIFFICULT_HABIT_BONUS_EXP)
                    .map(SkillEffectEntity::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        BigDecimal comboBonusPercent = effects.stream()
                .filter(effect -> effect.getEffectType() == SkillEffectType.DAILY_COMBO_BONUS_REWARD)
                .filter(effect -> context.sourceType() == RewardSourceType.TODO_COMPLETION)
                .map(effect -> effect.getValue()
                        .multiply(BigDecimal.valueOf(context.previousDailyCompletions()))
                        .min(effect.getConditionValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RpgReward(
                reward.exp() + percentage(reward.exp(), expBonusPercent.add(comboBonusPercent)),
                reward.coin() + percentage(reward.coin(), comboBonusPercent));
    }

    private long percentage(long base, BigDecimal percent) {
        return BigDecimal.valueOf(base).multiply(percent)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN).longValue();
    }
}
