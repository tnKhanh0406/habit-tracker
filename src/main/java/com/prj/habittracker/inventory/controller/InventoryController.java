package com.prj.habittracker.inventory.controller;

import com.prj.habittracker.inventory.dto.response.InventoryItemResponse;
import com.prj.habittracker.inventory.dto.response.UseItemResponse;
import com.prj.habittracker.inventory.service.InventoryService;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    public List<InventoryItemResponse> getInventory() {
        return inventoryService.getInventory();
    }

    @PostMapping("/{inventoryItemId}/use")
    public UseItemResponse useItem(@PathVariable UUID inventoryItemId) {
        return inventoryService.useItem(inventoryItemId);
    }

    @PostMapping("/{inventoryItemId}/equip")
    public CharacterResponse equipItem(@PathVariable UUID inventoryItemId) {
        return inventoryService.equipItem(inventoryItemId);
    }

    @PostMapping("/{inventoryItemId}/unequip")
    public CharacterResponse unequipItem(@PathVariable UUID inventoryItemId) {
        return inventoryService.unequipItem(inventoryItemId);
    }
}
