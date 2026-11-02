package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.RewardSourceType;
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
        if (context.sourceType() != RewardSourceType.HABIT_COMPLETION || context.streak() <= 0) {
            return reward;
        }
        var character = playerCharacterRepository.findByUserId(userId).orElse(null);
        if (character == null || character.getCharacterClass() == null) {
            return reward;
        }

        return skillRepository.findAllByCharacterClassAndSkillTypeAndEnabledTrue(
                        character.getCharacterClass(), SkillType.PASSIVE).stream()
                .flatMap(skill -> skill.getEffects().stream())
                .filter(effect -> effect.getEffectType() == SkillEffectType.STREAK_BONUS_EXP)
                .filter(effect -> effect.getConditionValue() != null
                        && context.streak() >= effect.getConditionValue().intValue())
                .max(Comparator.comparing(SkillEffectEntity::getConditionValue))
                .map(effect -> new RpgReward(
                        reward.exp() + percentage(reward.exp(), effect.getValue()), reward.coin()))
                .orElse(reward);
    }

    private long percentage(long base, BigDecimal percent) {
        return BigDecimal.valueOf(base).multiply(percent)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN).longValue();
    }
}
