package com.prj.habittracker.inventory.effect;

import com.prj.habittracker.item.entity.ConsumableDefinitionEntity;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.support.BaseCharacterStatService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsumableEffectHandlerTest {
    @Test
    void healthPotionCapsAtMaxHpAndRejectsUseAtFullHp() {
        PlayerCharacterEntity character = new PlayerCharacterEntity();
        character.setHp(90);
        character.setMaxHp(100);
        ConsumableDefinitionEntity potion = new ConsumableDefinitionEntity();
        potion.setEffectValue(BigDecimal.valueOf(20));
        RestoreHpConsumableHandler handler = new RestoreHpConsumableHandler(new BaseCharacterStatService());

        handler.apply(potion, character);
        assertThat(character.getHp()).isEqualTo(100);
        assertThatThrownBy(() -> handler.apply(potion, character)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void manaPotionCapsAtMaxMp() {
        PlayerCharacterEntity character = new PlayerCharacterEntity();
        character.setMp(45);
        character.setMaxMp(50);
        ConsumableDefinitionEntity potion = new ConsumableDefinitionEntity();
        potion.setEffectValue(BigDecimal.valueOf(15));

        new RestoreMpConsumableHandler(new BaseCharacterStatService()).apply(potion, character);

        assertThat(character.getMp()).isEqualTo(50);
    }
}
