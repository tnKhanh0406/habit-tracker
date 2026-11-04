package com.prj.habittracker.inventory.service;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.inventory.dto.response.InventoryItemResponse;
import com.prj.habittracker.inventory.dto.response.UseItemResponse;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
    List<InventoryItemResponse> getInventory();
    UseItemResponse useItem(UUID inventoryItemId);
    CharacterResponse equipItem(UUID inventoryItemId);
    CharacterResponse unequipItem(UUID inventoryItemId);
    void unequipIncompatibleItems(UUID userId, CharacterClass characterClass);
    void grantStarterPack(UUID userId, CharacterClass characterClass);
}
