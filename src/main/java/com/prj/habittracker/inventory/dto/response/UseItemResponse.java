package com.prj.habittracker.inventory.dto.response;

import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;

import java.util.UUID;

public record UseItemResponse(UUID inventoryItemId, int remainingQuantity, CharacterResponse character) {}
