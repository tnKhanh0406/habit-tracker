package com.prj.habittracker.shop.service.impl;

import com.prj.habittracker.common.enums.InventoryTransactionType;
import com.prj.habittracker.common.enums.ItemType;
import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.inventory.dto.response.ConsumableItemResponse;
import com.prj.habittracker.inventory.dto.response.EquipmentItemResponse;
import com.prj.habittracker.inventory.entity.InventoryItemEntity;
import com.prj.habittracker.inventory.entity.InventoryTransactionEntity;
import com.prj.habittracker.inventory.repository.InventoryItemRepository;
import com.prj.habittracker.inventory.repository.InventoryTransactionRepository;
import com.prj.habittracker.item.entity.EquipmentDefinitionEntity;
import com.prj.habittracker.item.entity.ItemEntity;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.shop.dto.request.PurchaseItemRequest;
import com.prj.habittracker.shop.dto.response.PurchaseItemResponse;
import com.prj.habittracker.shop.dto.response.ShopListingResponse;
import com.prj.habittracker.shop.entity.ShopListingEntity;
import com.prj.habittracker.shop.entity.ShopPurchaseEntity;
import com.prj.habittracker.shop.repository.ShopListingRepository;
import com.prj.habittracker.shop.repository.ShopPurchaseRepository;
import com.prj.habittracker.shop.service.ShopService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopListingRepository listingRepository;
    private final ShopPurchaseRepository purchaseRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final PlayerCharacterRepository characterRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public List<ShopListingResponse> getListings() {
        UUID userId = currentUserService.getCurrentUserId();
        PlayerCharacterEntity character = characterRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found"));
        Map<UUID, InventoryItemEntity> inventoryByItem = inventoryItemRepository
                .findAllByUserIdAndQuantityGreaterThanOrderByAcquiredAtAsc(userId, 0).stream()
                .collect(Collectors.toMap(entry -> entry.getItem().getId(), Function.identity()));

        return listingRepository.findAllByEnabledTrueAndItemEnabledTrueOrderBySortOrderAsc().stream()
                .map(listing -> toResponse(listing, character,
                        Optional.ofNullable(inventoryByItem.get(listing.getItem().getId()))
                                .map(InventoryItemEntity::getQuantity).orElse(0)))
                .toList();
    }

    @Override
    @Transactional
    public PurchaseItemResponse purchase(UUID listingId, PurchaseItemRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        PlayerCharacterEntity character = characterRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("Player character not found"));

        Optional<ShopPurchaseEntity> previous = purchaseRepository
                .findByUserIdAndIdempotencyKey(userId, request.idempotencyKey());
        if (previous.isPresent()) {
            ShopPurchaseEntity purchase = previous.get();
            if (!purchase.getListing().getId().equals(listingId) || purchase.getQuantity() != request.quantity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Idempotency key was already used for another purchase");
            }
            return toResponse(purchase);
        }

        ShopListingEntity listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop listing not found"));
        ItemEntity item = listing.getItem();
        validatePurchase(listing, character, request.quantity());

        InventoryItemEntity inventory = inventoryItemRepository
                .findByUserIdAndItemIdForUpdate(userId, item.getId())
                .orElseGet(() -> {
                    InventoryItemEntity created = new InventoryItemEntity();
                    created.setUserId(userId);
                    created.setItem(item);
                    created.setQuantity(0);
                    return created;
                });
        int resultingQuantity = inventory.getQuantity() + request.quantity();
        if (resultingQuantity > item.getMaxStack()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Purchase would exceed the stack limit of " + item.getMaxStack());
        }

        long totalCoin = multiplyPrice(listing.getPriceCoin(), request.quantity());
        long totalGem = multiplyPrice(listing.getPriceGem(), request.quantity());
        if (character.getCoin() < totalCoin) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough coins");
        }
        if (character.getGem() < totalGem) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough gems");
        }

        character.setCoin(character.getCoin() - totalCoin);
        character.setGem(character.getGem() - totalGem);
        inventory.setQuantity(resultingQuantity);
        inventoryItemRepository.save(inventory);
        if (listing.getStock() != null) listing.setStock(listing.getStock() - request.quantity());

        ShopPurchaseEntity purchase = new ShopPurchaseEntity();
        purchase.setUserId(userId);
        purchase.setListing(listing);
        purchase.setItem(item);
        purchase.setQuantity(request.quantity());
        purchase.setUnitCoinPrice(listing.getPriceCoin());
        purchase.setUnitGemPrice(listing.getPriceGem());
        purchase.setTotalCoin(totalCoin);
        purchase.setTotalGem(totalGem);
        purchase.setRemainingCoin(character.getCoin());
        purchase.setRemainingGem(character.getGem());
        purchase.setResultingInventoryQuantity(resultingQuantity);
        purchase.setIdempotencyKey(request.idempotencyKey());
        purchaseRepository.save(purchase);

        InventoryTransactionEntity transaction = new InventoryTransactionEntity();
        transaction.setUserId(userId);
        transaction.setItem(item);
        transaction.setQuantityDelta(request.quantity());
        transaction.setTransactionType(InventoryTransactionType.SHOP_PURCHASE);
        transaction.setSourceType("SHOP_PURCHASE");
        transaction.setSourceId(purchase.getId());
        transaction.setIdempotencyKey(request.idempotencyKey());
        transactionRepository.save(transaction);
        return toResponse(purchase);
    }

    private void validatePurchase(ShopListingEntity listing, PlayerCharacterEntity character, int quantity) {
        if (!listing.getEnabled() || !listing.getItem().getEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This item is no longer available");
        }
        if (character.getLevel() < listing.getRequiredLevel()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This item requires level " + listing.getRequiredLevel());
        }
        if (listing.getStock() != null && listing.getStock() < quantity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock");
        }
        EquipmentDefinitionEntity equipment = listing.getItem().getEquipmentDefinition();
        if (equipment != null && equipment.getRequiredClass() != null
                && equipment.getRequiredClass() != character.getCharacterClass()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This equipment requires class " + equipment.getRequiredClass());
        }
    }

    private long multiplyPrice(long unitPrice, int quantity) {
        try {
            return Math.multiplyExact(unitPrice, quantity);
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purchase total is too large");
        }
    }

    private ShopListingResponse toResponse(ShopListingEntity listing, PlayerCharacterEntity character,
                                           int ownedQuantity) {
        ItemEntity item = listing.getItem();
        String reason = unavailableReason(listing, character, ownedQuantity);
        EquipmentItemResponse equipment = item.getEquipmentDefinition() == null ? null : equipment(item);
        ConsumableItemResponse consumable = item.getConsumableDefinition() == null ? null
                : new ConsumableItemResponse(item.getConsumableDefinition().getEffectType(),
                item.getConsumableDefinition().getEffectValue());
        return new ShopListingResponse(listing.getId(), item.getId(), item.getCode(), item.getName(),
                item.getDescription(), item.getItemType(), item.getRarity(), item.getIcon(), item.getMaxStack(),
                listing.getPriceCoin(), listing.getPriceGem(), listing.getRequiredLevel(), listing.getStock(),
                ownedQuantity, reason == null, reason, equipment, consumable);
    }

    private String unavailableReason(ShopListingEntity listing, PlayerCharacterEntity character, int ownedQuantity) {
        ItemEntity item = listing.getItem();
        if (listing.getStock() != null && listing.getStock() <= 0) return "Out of stock";
        if (character.getLevel() < listing.getRequiredLevel()) return "Requires level " + listing.getRequiredLevel();
        if (item.getEquipmentDefinition() != null && item.getEquipmentDefinition().getRequiredClass() != null
                && item.getEquipmentDefinition().getRequiredClass() != character.getCharacterClass()) {
            return "Requires " + item.getEquipmentDefinition().getRequiredClass();
        }
        if (ownedQuantity >= item.getMaxStack()) return item.getItemType() == ItemType.EQUIPMENT
                ? "Already owned" : "Stack is full";
        if (character.getCoin() < listing.getPriceCoin()) return "Not enough coins";
        if (character.getGem() < listing.getPriceGem()) return "Not enough gems";
        return null;
    }

    private EquipmentItemResponse equipment(ItemEntity item) {
        EquipmentDefinitionEntity e = item.getEquipmentDefinition();
        return new EquipmentItemResponse(e.getSlot(), e.getRequiredClass(), e.getRequiredLevel(),
                e.getStrengthBonus(), e.getIntelligenceBonus(), e.getVitalityBonus(), e.getAgilityBonus(),
                e.getMaxHpBonus(), e.getMaxMpBonus(), e.getBossDamagePercent());
    }

    private PurchaseItemResponse toResponse(ShopPurchaseEntity purchase) {
        return new PurchaseItemResponse(purchase.getId(), purchase.getListing().getId(), purchase.getItem().getId(),
                purchase.getQuantity(), purchase.getResultingInventoryQuantity(), purchase.getRemainingCoin(),
                purchase.getRemainingGem());
    }
}
