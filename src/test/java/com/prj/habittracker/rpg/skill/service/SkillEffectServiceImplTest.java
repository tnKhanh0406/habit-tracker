package com.prj.habittracker.rpg.skill.service;

import com.prj.habittracker.common.enums.*;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.skill.effect.completion.BonusCoinEffectHandler;
import com.prj.habittracker.rpg.skill.effect.completion.BonusExpEffectHandler;
import com.prj.habittracker.rpg.skill.effect.completion.CompletionSkillEffectRegistry;
import com.prj.habittracker.rpg.skill.effect.penalty.BlockHpPenaltyEffectHandler;
import com.prj.habittracker.rpg.skill.effect.penalty.PenaltySkillEffectRegistry;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEntity;
import com.prj.habittracker.rpg.skill.repository.SkillCastRepository;
import com.prj.habittracker.rpg.skill.service.impl.SkillEffectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SkillEffectServiceImplTest {
    private SkillCastRepository repository;
    private SkillEffectService service;

    @BeforeEach
    void setUp() {
        repository = mock(SkillCastRepository.class);
        service = new SkillEffectServiceImpl(
                repository,
                new CompletionSkillEffectRegistry(List.of(new BonusExpEffectHandler(), new BonusCoinEffectHandler())),
                new PenaltySkillEffectRegistry(List.of(new BlockHpPenaltyEffectHandler()))
        );
    }

    @Test
    void focusAddsExpAndConsumesCast() {
        SkillCastEntity cast = castWithEffect(SkillEffectType.BONUS_EXP, "25");
        when(repository.findAllActive(any(), any())).thenReturn(List.of(cast));

        RpgReward reward = service.applyCompletionEffects(
                UUID.randomUUID(), RewardSourceType.HABIT_COMPLETION, UUID.randomUUID(), new RpgReward(20, 10));

        assertThat(reward.exp()).isEqualTo(25);
        assertThat(reward.coin()).isEqualTo(10);
        assertThat(cast.getStatus()).isEqualTo(SkillCastStatus.CONSUMED);
        assertThat(cast.getConsumedAt()).isNotNull();
    }

    @Test
    void bountyOnlyAppliesToSelectedTodo() {
        UUID selectedTodo = UUID.randomUUID();
        SkillCastEntity cast = castWithEffect(SkillEffectType.BONUS_COIN, "50");
        cast.setTargetId(selectedTodo);
        when(repository.findAllActive(any(), any())).thenReturn(List.of(cast));

        RpgReward otherTodo = service.applyCompletionEffects(
                UUID.randomUUID(), RewardSourceType.TODO_COMPLETION, UUID.randomUUID(), new RpgReward(10, 12));
        assertThat(otherTodo.coin()).isEqualTo(12);
        assertThat(cast.getStatus()).isEqualTo(SkillCastStatus.ACTIVE);

        RpgReward selected = service.applyCompletionEffects(
                UUID.randomUUID(), RewardSourceType.TODO_COMPLETION, selectedTodo, new RpgReward(10, 12));
        assertThat(selected.coin()).isEqualTo(18);
        assertThat(cast.getStatus()).isEqualTo(SkillCastStatus.CONSUMED);
    }

    @Test
    void ironWillBlocksOneDamageAndConsumesCast() {
        SkillCastEntity cast = castWithEffect(SkillEffectType.BLOCK_HP_PENALTY, "1");
        when(repository.findAllActive(any(), any())).thenReturn(List.of(cast));

        int damage = service.applyPenaltyEffects(UUID.randomUUID(), 3);

        assertThat(damage).isEqualTo(2);
        assertThat(cast.getStatus()).isEqualTo(SkillCastStatus.CONSUMED);
    }

    private SkillCastEntity castWithEffect(SkillEffectType type, String value) {
        SkillEntity skill = new SkillEntity();
        SkillEffectEntity effect = new SkillEffectEntity();
        effect.setSkill(skill);
        effect.setEffectType(type);
        effect.setValue(new BigDecimal(value));
        effect.setValueType(type == SkillEffectType.BLOCK_HP_PENALTY ? SkillValueType.FLAT : SkillValueType.PERCENT);
        skill.setEffects(List.of(effect));

        SkillCastEntity cast = new SkillCastEntity();
        cast.setSkill(skill);
        cast.setStatus(SkillCastStatus.ACTIVE);
        cast.setCastAt(OffsetDateTime.now());
        return cast;
    }
}
