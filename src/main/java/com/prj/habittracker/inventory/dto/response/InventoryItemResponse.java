package com.prj.habittracker.inventory.dto.response;

import com.prj.habittracker.common.enums.ItemRarity;
import com.prj.habittracker.common.enums.ItemType;

import java.util.UUID;

public record InventoryItemResponse(
        UUID inventoryItemId,
        UUID itemId,
        String code,
        String name,
        String description,
        ItemType itemType,
        ItemRarity rarity,
        String icon,
        boolean stackable,
        int quantity,
        EquipmentItemResponse equipment,
        ConsumableItemResponse consumable
) {}
