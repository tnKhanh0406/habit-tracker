package com.prj.habittracker.inventory.service.impl;

import com.prj.habittracker.common.enums.*;
import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.inventory.dto.response.*;
import com.prj.habittracker.inventory.effect.ConsumableEffectRegistry;
import com.prj.habittracker.inventory.entity.InventoryItemEntity;
import com.prj.habittracker.inventory.entity.InventoryTransactionEntity;
import com.prj.habittracker.inventory.entity.EquippedItemEntity;
import com.prj.habittracker.inventory.repository.EquippedItemRepository;
import com.prj.habittracker.inventory.repository.InventoryItemRepository;
import com.prj.habittracker.inventory.repository.InventoryTransactionRepository;
import com.prj.habittracker.inventory.service.InventoryService;
import com.prj.habittracker.item.entity.ItemEntity;
import com.prj.habittracker.item.entity.EquipmentDefinitionEntity;
import com.prj.habittracker.item.repository.ItemRepository;
import com.prj.habittracker.rpg.character.config.LevelPolicy;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private static final Map<CharacterClass, List<String>> CLASS_STARTER_ITEMS = Map.of(
            CharacterClass.WARRIOR, List.of("WARRIOR_IRON_SWORD", "WARRIOR_GUARD_PLATE", "WARRIOR_VANGUARD_EMBLEM"),
            CharacterClass.MAGE, List.of("MAGE_ASH_WAND", "MAGE_SCHOLAR_ROBE", "MAGE_ARCANE_RING"),
            CharacterClass.RANGER, List.of("RANGER_LONGBOW", "RANGER_TRAIL_JACKET", "RANGER_HAWK_CHARM"),
            CharacterClass.CLERIC, List.of("CLERIC_OAK_STAFF", "CLERIC_ACOLYTE_ROBE", "CLERIC_BLESSED_PENDANT")
    );

    private final InventoryItemRepository inventoryItemRepository;
    private final EquippedItemRepository equippedItemRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ItemRepository itemRepository;
    private final PlayerCharacterRepository playerCharacterRepository;
    private final CurrentUserService currentUserService;
    private final ConsumableEffectRegistry consumableEffectRegistry;
    private final LevelPolicy levelPolicy;

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getInventory() {
        UUID userId = currentUserService.getCurrentUserId();
        Set<UUID> equippedIds = equippedItemRepository.findAllByUserId(userId).stream()
                .map(equipped -> equipped.getInventoryItem().getId())
                .collect(Collectors.toSet());
        return inventoryItemRepository
                .findAllByUserIdAndQuantityGreaterThanOrderByAcquiredAtAsc(userId, 0)
                .stream().map(inventory -> toResponse(inventory, equippedIds.contains(inventory.getId()))).toList();
    }

    @Override
    @Transactional
    public UseItemResponse useItem(UUID inventoryItemId) {
        UUID userId = currentUserService.getCurrentUserId();
        InventoryItemEntity inventory = inventoryItemRepository.findByIdAndUserIdForUpdate(inventoryItemId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found"));
        if (inventory.getQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item is out of stock");
        }

        ItemEntity item = inventory.getItem();
        if (item.getItemType() != ItemType.CONSUMABLE || item.getConsumableDefinition() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This item cannot be consumed");
        }
        var character = playerCharacterRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found"));
        consumableEffectRegistry.get(item.getConsumableDefinition().getEffectType())
                .apply(item.getConsumableDefinition(), character);

        inventory.setQuantity(inventory.getQuantity() - 1);
        record(userId, item, -1, InventoryTransactionType.CONSUME, "INVENTORY_ITEM", inventory.getId());
        return new UseItemResponse(inventory.getId(), inventory.getQuantity(),
                CharacterResponse.from(character, levelPolicy.requiredExp(character.getLevel())));
    }

    @Override
    @Transactional
    public void grantStarterPack(UUID userId, CharacterClass characterClass) {
        CLASS_STARTER_ITEMS.get(characterClass).forEach(code -> addItem(userId, code, 1, InventoryTransactionType.STARTER_GRANT));
        addItem(userId, "MINOR_HEALTH_POTION", 2, InventoryTransactionType.STARTER_GRANT);
        addItem(userId, "MINOR_MANA_POTION", 2, InventoryTransactionType.STARTER_GRANT);
    }

    @Override
    @Transactional
    public CharacterResponse equipItem(UUID inventoryItemId) {
        UUID userId = currentUserService.getCurrentUserId();
        var character = playerCharacterRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found"));
        InventoryItemEntity inventory = inventoryItemRepository.findByIdAndUserIdForUpdate(inventoryItemId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found"));
        EquipmentDefinitionEntity equipment = requireEquipment(inventory);
        if (equipment.getRequiredClass() != null && equipment.getRequiredClass() != character.getCharacterClass()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This equipment requires class " + equipment.getRequiredClass());
        }
        if (character.getLevel() < equipment.getRequiredLevel()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This equipment requires level " + equipment.getRequiredLevel());
        }

        var sameItem = equippedItemRepository.findByUserIdAndInventoryItemIdForUpdate(userId, inventoryItemId);
        if (sameItem.isPresent()) {
            return response(character);
        }
        equippedItemRepository.findByUserIdAndSlotForUpdate(userId, equipment.getSlot()).ifPresent(current -> {
            applyBonuses(character, current.getInventoryItem().getItem().getEquipmentDefinition(), -1);
            equippedItemRepository.delete(current);
            equippedItemRepository.flush();
        });

        EquippedItemEntity equipped = new EquippedItemEntity();
        equipped.setUserId(userId);
        equipped.setInventoryItem(inventory);
        equipped.setSlot(equipment.getSlot());
        equippedItemRepository.save(equipped);
        applyBonuses(character, equipment, 1);
        return response(character);
    }

    @Override
    @Transactional
    public CharacterResponse unequipItem(UUID inventoryItemId) {
        UUID userId = currentUserService.getCurrentUserId();
        var character = playerCharacterRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found"));
        EquippedItemEntity equipped = equippedItemRepository
                .findByUserIdAndInventoryItemIdForUpdate(userId, inventoryItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Item is not equipped"));
        applyBonuses(character, equipped.getInventoryItem().getItem().getEquipmentDefinition(), -1);
        equippedItemRepository.delete(equipped);
        return response(character);
    }

    @Override
    @Transactional
    public void unequipIncompatibleItems(UUID userId, CharacterClass characterClass) {
        var character = playerCharacterRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found"));
        equippedItemRepository.findAllByUserId(userId).stream()
                .filter(equipped -> {
                    CharacterClass required = equipped.getInventoryItem().getItem().getEquipmentDefinition().getRequiredClass();
                    return required != null && required != characterClass;
                })
                .forEach(equipped -> {
                    applyBonuses(character, equipped.getInventoryItem().getItem().getEquipmentDefinition(), -1);
                    equippedItemRepository.delete(equipped);
                });
    }

    private void addItem(UUID userId, String code, int quantity, InventoryTransactionType transactionType) {
        ItemEntity item = itemRepository.findByCodeAndEnabledTrue(code)
                .orElseThrow(() -> new IllegalStateException("Enabled item not found: " + code));
        InventoryItemEntity inventory = inventoryItemRepository.findByUserIdAndItemIdForUpdate(userId, item.getId())
                .orElseGet(() -> {
                    InventoryItemEntity created = new InventoryItemEntity();
                    created.setUserId(userId);
                    created.setItem(item);
                    created.setQuantity(0);
                    return created;
                });
        int newQuantity = inventory.getQuantity() + quantity;
        if (newQuantity > item.getMaxStack()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item stack limit exceeded for " + item.getName());
        }
        inventory.setQuantity(newQuantity);
        inventoryItemRepository.save(inventory);
        record(userId, item, quantity, transactionType, "CHARACTER_CLASS", null);
    }

    private void record(UUID userId, ItemEntity item, int delta, InventoryTransactionType type,
                        String sourceType, UUID sourceId) {
        InventoryTransactionEntity transaction = new InventoryTransactionEntity();
        transaction.setUserId(userId);
        transaction.setItem(item);
        transaction.setQuantityDelta(delta);
        transaction.setTransactionType(type);
        transaction.setSourceType(sourceType);
        transaction.setSourceId(sourceId);
        transactionRepository.save(transaction);
    }

    private EquipmentDefinitionEntity requireEquipment(InventoryItemEntity inventory) {
        if (inventory.getQuantity() <= 0 || inventory.getItem().getItemType() != ItemType.EQUIPMENT
                || inventory.getItem().getEquipmentDefinition() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This item cannot be equipped");
        }
        return inventory.getItem().getEquipmentDefinition();
    }

    private void applyBonuses(PlayerCharacterEntity character,
                              EquipmentDefinitionEntity equipment, int multiplier) {
        character.setStrength(character.getStrength() + equipment.getStrengthBonus() * multiplier);
        character.setIntelligence(character.getIntelligence() + equipment.getIntelligenceBonus() * multiplier);
        character.setVitality(character.getVitality() + equipment.getVitalityBonus() * multiplier);
        character.setAgility(character.getAgility() + equipment.getAgilityBonus() * multiplier);
        character.setMaxHp(character.getMaxHp() + equipment.getMaxHpBonus() * multiplier);
        character.setMaxMp(character.getMaxMp() + equipment.getMaxMpBonus() * multiplier);
        character.setHp(Math.min(character.getHp(), character.getMaxHp()));
        character.setMp(Math.min(character.getMp(), character.getMaxMp()));
    }

    private CharacterResponse response(PlayerCharacterEntity character) {
        return CharacterResponse.from(character, levelPolicy.requiredExp(character.getLevel()));
    }

    private InventoryItemResponse toResponse(InventoryItemEntity inventory, boolean equipped) {
        ItemEntity item = inventory.getItem();
        EquipmentItemResponse equipment = item.getEquipmentDefinition() == null ? null : new EquipmentItemResponse(
                item.getEquipmentDefinition().getSlot(), item.getEquipmentDefinition().getRequiredClass(),
                item.getEquipmentDefinition().getRequiredLevel(), item.getEquipmentDefinition().getStrengthBonus(),
                item.getEquipmentDefinition().getIntelligenceBonus(), item.getEquipmentDefinition().getVitalityBonus(),
                item.getEquipmentDefinition().getAgilityBonus(), item.getEquipmentDefinition().getMaxHpBonus(),
                item.getEquipmentDefinition().getMaxMpBonus(), item.getEquipmentDefinition().getBossDamagePercent());
        ConsumableItemResponse consumable = item.getConsumableDefinition() == null ? null : new ConsumableItemResponse(
                item.getConsumableDefinition().getEffectType(), item.getConsumableDefinition().getEffectValue());
        return new InventoryItemResponse(inventory.getId(), item.getId(), item.getCode(), item.getName(),
                item.getDescription(), item.getItemType(), item.getRarity(), item.getIcon(), item.getStackable(),
                inventory.getQuantity(), equipped, equipment, consumable);
    }
}
