package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.service.impl.ManaServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ManaServiceImplTest {
    private final ManaService service = new ManaServiceImpl();

    @Test
    void regeneratesTenManaPerDayWithoutExceedingMaximum() {
        PlayerCharacterEntity character = PlayerCharacterEntity.builder()
                .mp(35).maxMp(50).manaRegeneratedAt(LocalDate.of(2026, 10, 25)).build();

        service.regenerate(character, LocalDate.of(2026, 10, 27));

        assertThat(character.getMp()).isEqualTo(50);
        assertThat(character.getManaRegeneratedAt()).isEqualTo(LocalDate.of(2026, 10, 27));
    }

    @Test
    void repeatedRegenerationOnSameDayDoesNotRestoreAgain() {
        PlayerCharacterEntity character = PlayerCharacterEntity.builder()
                .mp(20).maxMp(50).manaRegeneratedAt(LocalDate.of(2026, 10, 27)).build();

        service.regenerate(character, LocalDate.of(2026, 10, 27));

        assertThat(character.getMp()).isEqualTo(20);
    }

    @Test
    void spendRejectsInsufficientManaWithoutChangingBalance() {
        PlayerCharacterEntity character = PlayerCharacterEntity.builder().mp(10).maxMp(50).build();

        assertThatThrownBy(() -> service.spend(character, 20))
                .hasMessageContaining("Not enough mana");
        assertThat(character.getMp()).isEqualTo(10);
    }
}
