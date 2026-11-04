package com.prj.habittracker.inventory.controller;

import com.prj.habittracker.inventory.dto.response.InventoryItemResponse;
import com.prj.habittracker.inventory.dto.response.UseItemResponse;
import com.prj.habittracker.inventory.service.InventoryService;
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
}
