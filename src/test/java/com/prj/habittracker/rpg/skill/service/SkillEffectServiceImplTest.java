package com.prj.habittracker.rpg.skill.service;

import com.prj.habittracker.common.enums.*;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.skill.effect.completion.BonusCoinEffectHandler;
import com.prj.habittracker.rpg.skill.effect.completion.BonusExpEffectHandler;
import com.prj.habittracker.rpg.skill.effect.completion.CompletionSkillEffectRegistry;
import com.prj.habittracker.rpg.skill.effect.completion.PassiveCompletionEffectService;
import com.prj.habittracker.rpg.skill.effect.penalty.BlockHpPenaltyEffectHandler;
import com.prj.habittracker.rpg.skill.effect.penalty.ProtectStreakEffectHandler;
import com.prj.habittracker.rpg.skill.effect.penalty.PenaltySkillEffectRegistry;
import com.prj.habittracker.rpg.skill.entity.SkillCastEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEntity;
import com.prj.habittracker.rpg.skill.repository.SkillCastRepository;
import com.prj.habittracker.rpg.skill.repository.SkillRepository;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.skill.effect.completion.CompletionEffectContext;
import com.prj.habittracker.rpg.skill.service.impl.SkillEffectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.lang.reflect.Proxy;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
class SkillEffectServiceImplTest {
    private SkillCastRepository repository;
    private SkillEffectService service;
    private List<SkillCastEntity> activeCasts;

    @BeforeEach
    void setUp() {
        activeCasts = new ArrayList<>();
        repository = (SkillCastRepository) Proxy.newProxyInstance(
                SkillCastRepository.class.getClassLoader(),
                new Class<?>[]{SkillCastRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findAllActive")) return List.copyOf(activeCasts);
                    throw new UnsupportedOperationException("Unexpected repository call: " + method.getName());
                }
        );
        service = new SkillEffectServiceImpl(
                repository,
                new CompletionSkillEffectRegistry(List.of(new BonusExpEffectHandler(), new BonusCoinEffectHandler())),
                new PenaltySkillEffectRegistry(List.of(new BlockHpPenaltyEffectHandler(), new ProtectStreakEffectHandler())),
                new PassiveCompletionEffectService(emptyPlayerRepository(), emptySkillRepository())
        );
    }

    @Test
    void focusAddsExpAndConsumesCast() {
        SkillCastEntity cast = castWithEffect(SkillEffectType.BONUS_EXP, "25");
        activeCasts.add(cast);

        RpgReward reward = service.applyCompletionEffects(
                UUID.randomUUID(), RewardSourceType.HABIT_COMPLETION, UUID.randomUUID(), 0, new RpgReward(20, 10));

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
        cast.setTargetType(SkillTargetType.TODO);
        activeCasts.add(cast);

        RpgReward otherTodo = service.applyCompletionEffects(
                UUID.randomUUID(), RewardSourceType.TODO_COMPLETION, UUID.randomUUID(), 0, new RpgReward(10, 12));
        assertThat(otherTodo.coin()).isEqualTo(12);
        assertThat(cast.getStatus()).isEqualTo(SkillCastStatus.ACTIVE);

        RpgReward selected = service.applyCompletionEffects(
                UUID.randomUUID(), RewardSourceType.TODO_COMPLETION, selectedTodo, 0, new RpgReward(10, 12));
        assertThat(selected.coin()).isEqualTo(18);
        assertThat(cast.getStatus()).isEqualTo(SkillCastStatus.CONSUMED);
    }

    @Test
    void eagleEyeDoublesExpAndCoinForSelectedTodoAndConsumesOnce() {
        UUID selectedTodo = UUID.randomUUID();
        SkillCastEntity cast = castWithEffects(
                effect(SkillEffectType.BONUS_EXP, "100"),
                effect(SkillEffectType.BONUS_COIN, "100")
        );
        cast.setTargetType(SkillTargetType.TODO);
        cast.setTargetId(selectedTodo);
        activeCasts.add(cast);

        RpgReward wrongTarget = service.applyCompletionEffects(
                UUID.randomUUID(), RewardSourceType.TODO_COMPLETION, UUID.randomUUID(), 0, new RpgReward(10, 12));
        assertThat(wrongTarget).isEqualTo(new RpgReward(10, 12));
        assertThat(cast.getStatus()).isEqualTo(SkillCastStatus.ACTIVE);

        RpgReward reward = service.applyCompletionEffects(
                UUID.randomUUID(), RewardSourceType.TODO_COMPLETION, selectedTodo, 0, new RpgReward(10, 12));

        assertThat(reward.exp()).isEqualTo(20);
        assertThat(reward.coin()).isEqualTo(24);
        assertThat(cast.getStatus()).isEqualTo(SkillCastStatus.CONSUMED);
    }

    @Test
    void ironWillBlocksOneDamageAndConsumesCast() {
        SkillCastEntity cast = castWithEffect(SkillEffectType.BLOCK_HP_PENALTY, "1");
        cast.setTargetType(SkillTargetType.SELF);
        activeCasts.add(cast);

        int damage = service.applyPenaltyEffects(UUID.randomUUID(), 3, Set.of()).damage();

        assertThat(damage).isEqualTo(2);
        assertThat(cast.getStatus()).isEqualTo(SkillCastStatus.CONSUMED);
    }

    @Test
    void lastStandBlocksDamageAndProtectsTargetStreakThenConsumesOnce() {
        UUID habitId = UUID.randomUUID();
        SkillCastEntity cast = castWithEffects(
                effect(SkillEffectType.BLOCK_HP_PENALTY, "1"),
                effect(SkillEffectType.PROTECT_STREAK, "1")
        );
        cast.setTargetType(SkillTargetType.HABIT);
        cast.setTargetId(habitId);
        activeCasts.add(cast);

        var adjustment = service.applyPenaltyEffects(UUID.randomUUID(), 2, Set.of(habitId));

        assertThat(adjustment.damage()).isEqualTo(1);
        assertThat(adjustment.protectedHabitIds()).containsExactly(habitId);
        assertThat(cast.getStatus()).isEqualTo(SkillCastStatus.CONSUMED);
    }

    @Test
    void battleDisciplineUsesOnlyHighestReachedTier() {
        PlayerCharacterEntity character = new PlayerCharacterEntity();
        character.setCharacterClass(CharacterClass.WARRIOR);
        SkillEntity passive = new SkillEntity();
        passive.setEffects(List.of(
                streakEffect("5", "7"), streakEffect("10", "14"), streakEffect("20", "30")));
        PlayerCharacterRepository players = (PlayerCharacterRepository) Proxy.newProxyInstance(
                PlayerCharacterRepository.class.getClassLoader(), new Class<?>[]{PlayerCharacterRepository.class},
                (proxy, method, args) -> method.getName().equals("findByUserId")
                        ? Optional.of(character) : defaultValue(method.getReturnType()));
        SkillRepository skillRepository = (SkillRepository) Proxy.newProxyInstance(
                SkillRepository.class.getClassLoader(), new Class<?>[]{SkillRepository.class},
                (proxy, method, args) -> method.getName().startsWith("findAllByCharacterClassAndSkillType")
                        ? List.of(passive) : defaultValue(method.getReturnType()));
        PassiveCompletionEffectService passiveService = new PassiveCompletionEffectService(players, skillRepository);

        RpgReward reward = passiveService.apply(UUID.randomUUID(),
                new CompletionEffectContext(RewardSourceType.HABIT_COMPLETION, UUID.randomUUID(), 14),
                new RpgReward(100, 10));

        assertThat(reward).isEqualTo(new RpgReward(110, 10));
    }

    private SkillEffectEntity streakEffect(String value, String condition) {
        SkillEffectEntity effect = effect(SkillEffectType.STREAK_BONUS_EXP, value);
        effect.setConditionValue(new BigDecimal(condition));
        return effect;
    }

    private PlayerCharacterRepository emptyPlayerRepository() {
        return (PlayerCharacterRepository) Proxy.newProxyInstance(
                PlayerCharacterRepository.class.getClassLoader(), new Class<?>[]{PlayerCharacterRepository.class},
                (proxy, method, args) -> method.getName().equals("findByUserId")
                        ? Optional.empty() : defaultValue(method.getReturnType()));
    }

    private SkillRepository emptySkillRepository() {
        return (SkillRepository) Proxy.newProxyInstance(
                SkillRepository.class.getClassLoader(), new Class<?>[]{SkillRepository.class},
                (proxy, method, args) -> method.getName().startsWith("findAllByCharacterClassAndSkillType")
                        ? List.of() : defaultValue(method.getReturnType()));
    }

    private Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        return null;
    }

    private SkillCastEntity castWithEffect(SkillEffectType type, String value) {
        SkillCastEntity cast = castWithEffects(effect(type, value));
        if (type == SkillEffectType.BONUS_EXP) {
            cast.setTargetType(SkillTargetType.NEXT_COMPLETION);
        }
        return cast;
    }

    private SkillEffectEntity effect(SkillEffectType type, String value) {
        SkillEffectEntity effect = new SkillEffectEntity();
        effect.setEffectType(type);
        effect.setValue(new BigDecimal(value));
        effect.setValueType(type == SkillEffectType.BLOCK_HP_PENALTY ? SkillValueType.FLAT : SkillValueType.PERCENT);
        return effect;
    }

    private SkillCastEntity castWithEffects(SkillEffectEntity... effects) {
        SkillEntity skill = new SkillEntity();
        for (SkillEffectEntity effect : effects) effect.setSkill(skill);
        skill.setEffects(List.of(effects));

        SkillCastEntity cast = new SkillCastEntity();
        cast.setSkill(skill);
        cast.setStatus(SkillCastStatus.ACTIVE);
        cast.setCastAt(OffsetDateTime.now());
        return cast;
    }
}
