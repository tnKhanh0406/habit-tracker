package com.prj.habittracker.inventory.service;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.inventory.dto.response.InventoryItemResponse;
import com.prj.habittracker.inventory.dto.response.UseItemResponse;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
    List<InventoryItemResponse> getInventory();
    UseItemResponse useItem(UUID inventoryItemId);
    void grantStarterPack(UUID userId, CharacterClass characterClass);
}
