package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.inventory.entity.EquippedItemEntity;
import com.prj.habittracker.inventory.entity.InventoryItemEntity;
import com.prj.habittracker.inventory.repository.EquippedItemRepository;
import com.prj.habittracker.item.entity.EquipmentDefinitionEntity;
import com.prj.habittracker.item.entity.ItemEntity;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.service.impl.CharacterStatServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CharacterStatServiceImplTest {
    @Test
    void calculatesEffectiveStatsWithoutMutatingBaseStats() {
        UUID userId = UUID.randomUUID();
        PlayerCharacterEntity character = PlayerCharacterEntity.builder()
                .userId(userId).strength(2).intelligence(3).vitality(4).agility(5)
                .hp(108).maxHp(100).mp(50).maxMp(50).build();
        EquipmentDefinitionEntity definition = new EquipmentDefinitionEntity();
        definition.setStrengthBonus(3);
        definition.setIntelligenceBonus(1);
        definition.setVitalityBonus(0);
        definition.setAgilityBonus(2);
        definition.setMaxHpBonus(10);
        definition.setMaxMpBonus(5);
        definition.setBossDamagePercent(new BigDecimal("5.00"));
        ItemEntity item = new ItemEntity();
        item.setEquipmentDefinition(definition);
        InventoryItemEntity inventory = new InventoryItemEntity();
        inventory.setItem(item);
        EquippedItemEntity equipped = new EquippedItemEntity();
        equipped.setInventoryItem(inventory);
        var repository = (EquippedItemRepository) Proxy.newProxyInstance(
                EquippedItemRepository.class.getClassLoader(), new Class<?>[]{EquippedItemRepository.class},
                (proxy, method, args) -> method.getName().equals("findAllByUserId") ? List.of(equipped) : null);
        var service = new CharacterStatServiceImpl(repository);

        var result = service.calculate(character);

        assertThat(result.strength()).isEqualTo(5);
        assertThat(result.intelligence()).isEqualTo(4);
        assertThat(result.agility()).isEqualTo(7);
        assertThat(result.maxHp()).isEqualTo(110);
        assertThat(result.maxMp()).isEqualTo(55);
        assertThat(result.bossDamagePercent()).isEqualByComparingTo("5.00");
        assertThat(character.getStrength()).isEqualTo(2);
        assertThat(character.getMaxHp()).isEqualTo(100);
    }
}
