package com.prj.habittracker.rpg.skill.effect.instant;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.junit.jupiter.api.Test;
import com.prj.habittracker.support.BaseCharacterStatService;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OverhealToCoinEffectHandlerTest {
    private final OverhealToCoinEffectHandler handler = new OverhealToCoinEffectHandler(new BaseCharacterStatService());

    @Test
    void convertsOnlyOverhealingToCoin() {
        PlayerCharacterEntity character = PlayerCharacterEntity.builder()
                .hp(90).maxHp(100).coin(5L).build();
        SkillEffectEntity effect = new SkillEffectEntity();
        effect.setValue(BigDecimal.valueOf(20));

        handler.apply(effect, character);

        assertThat(character.getHp()).isEqualTo(100);
        assertThat(character.getCoin()).isEqualTo(15);
    }

    @Test
    void fullHealthConvertsTheWholeHealToCoin() {
        PlayerCharacterEntity character = PlayerCharacterEntity.builder()
                .hp(100).maxHp(100).coin(10L).build();
        SkillEffectEntity effect = new SkillEffectEntity();
        effect.setValue(BigDecimal.valueOf(20));

        handler.apply(effect, character);

        assertThat(character.getHp()).isEqualTo(100);
        assertThat(character.getCoin()).isEqualTo(30);
    }
}
