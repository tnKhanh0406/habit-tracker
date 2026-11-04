package com.prj.habittracker.shop.dto.response;

import java.util.UUID;

public record PurchaseItemResponse(
        UUID purchaseId,
        UUID listingId,
        UUID itemId,
        int purchasedQuantity,
        int inventoryQuantity,
        long coin,
        long gem
) {}
