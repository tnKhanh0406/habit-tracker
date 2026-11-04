package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.rpg.character.config.LevelPolicy;
import com.prj.habittracker.rpg.character.service.impl.CharacterProgressionServiceImpl;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class KnockoutTest {
    @Test
    void excessiveDamageTriggersOnlyOneCoinPenaltyAndRevives() {
        var character = PlayerCharacterEntity.builder().level(1).exp(0L).hp(2).maxHp(100)
                .mp(30).maxMp(50).coin(1000L).gem(0L).strength(1).intelligence(1)
                .vitality(1).agility(1).statPoints(0).build();
        var repository = (PlayerCharacterRepository) Proxy.newProxyInstance(
                PlayerCharacterRepository.class.getClassLoader(), new Class<?>[]{PlayerCharacterRepository.class},
                (proxy, method, args) -> Optional.of(character));
        var service = new CharacterProgressionServiceImpl(repository, new LevelPolicy());
        var result = service.applyHpDamage(UUID.randomUUID(), 150);
        assertThat(result.hp()).isEqualTo(50);
        assertThat(result.coin()).isEqualTo(950);
        assertThat(result.mp()).isEqualTo(30);
    }
}
