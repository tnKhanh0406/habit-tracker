package com.prj.habittracker.shop.dto.response;

import com.prj.habittracker.common.enums.*;
import com.prj.habittracker.inventory.dto.response.ConsumableItemResponse;
import com.prj.habittracker.inventory.dto.response.EquipmentItemResponse;

import java.util.UUID;

public record ShopListingResponse(
        UUID listingId,
        UUID itemId,
        String code,
        String name,
        String description,
        ItemType itemType,
        ItemRarity rarity,
        String icon,
        int maxStack,
        long priceCoin,
        long priceGem,
        int requiredLevel,
        Integer stock,
        int ownedQuantity,
        boolean canPurchase,
        String unavailableReason,
        EquipmentItemResponse equipment,
        ConsumableItemResponse consumable
) {}
