package com.prj.habittracker.rpg.skill.effect.instant;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.junit.jupiter.api.Test;
import com.prj.habittracker.support.BaseCharacterStatService;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RestoreManaEffectHandlerTest {
    private final RestoreManaEffectHandler handler = new RestoreManaEffectHandler(new BaseCharacterStatService());

    @Test
    void restoresManaWithoutExceedingMaximum() {
        PlayerCharacterEntity character = new PlayerCharacterEntity();
        character.setMp(90);
        character.setMaxMp(100);
        SkillEffectEntity effect = new SkillEffectEntity();
        effect.setValue(BigDecimal.valueOf(20));

        handler.apply(effect, character);

        assertThat(character.getMp()).isEqualTo(100);
    }
}
