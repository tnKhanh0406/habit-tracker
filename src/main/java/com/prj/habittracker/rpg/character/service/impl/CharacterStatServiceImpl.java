package com.prj.habittracker.rpg.character.service.impl;

import com.prj.habittracker.inventory.repository.EquippedItemRepository;
import com.prj.habittracker.item.entity.EquipmentDefinitionEntity;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.model.EffectiveCharacterStats;
import com.prj.habittracker.rpg.character.service.CharacterStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CharacterStatServiceImpl implements CharacterStatService {
    private final EquippedItemRepository equippedItemRepository;

    @Override
    @Transactional(readOnly = true)
    public EffectiveCharacterStats calculate(PlayerCharacterEntity character) {
        int strength = character.getStrength();
        int intelligence = character.getIntelligence();
        int vitality = character.getVitality();
        int agility = character.getAgility();
        int maxHp = character.getMaxHp();
        int maxMp = character.getMaxMp();
        BigDecimal bossDamage = BigDecimal.ZERO;

        for (var equipped : equippedItemRepository.findAllByUserId(character.getUserId())) {
            EquipmentDefinitionEntity equipment = equipped.getInventoryItem().getItem().getEquipmentDefinition();
            strength += equipment.getStrengthBonus();
            intelligence += equipment.getIntelligenceBonus();
            vitality += equipment.getVitalityBonus();
            agility += equipment.getAgilityBonus();
            maxHp += equipment.getMaxHpBonus();
            maxMp += equipment.getMaxMpBonus();
            bossDamage = bossDamage.add(equipment.getBossDamagePercent());
        }
        return new EffectiveCharacterStats(strength, intelligence, vitality, agility, maxHp, maxMp, bossDamage);
    }

    @Override
    public void clampResources(PlayerCharacterEntity character) {
        EffectiveCharacterStats stats = calculate(character);
        character.setHp(Math.min(character.getHp(), stats.maxHp()));
        character.setMp(Math.min(character.getMp(), stats.maxMp()));
    }
}
