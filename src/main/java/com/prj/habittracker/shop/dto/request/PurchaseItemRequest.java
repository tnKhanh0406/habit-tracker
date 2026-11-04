package com.prj.habittracker.shop.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PurchaseItemRequest(
        @Min(1) @Max(20) int quantity,
        @NotNull UUID idempotencyKey
) {}
