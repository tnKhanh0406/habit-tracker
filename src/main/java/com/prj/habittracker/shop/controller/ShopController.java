package com.prj.habittracker.shop.controller;

import com.prj.habittracker.shop.dto.request.PurchaseItemRequest;
import com.prj.habittracker.shop.dto.response.PurchaseItemResponse;
import com.prj.habittracker.shop.dto.response.ShopListingResponse;
import com.prj.habittracker.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @GetMapping
    public List<ShopListingResponse> getListings() {
        return shopService.getListings();
    }

    @PostMapping("/{listingId}/purchase")
    public PurchaseItemResponse purchase(@PathVariable UUID listingId,
                                         @Valid @RequestBody PurchaseItemRequest request) {
        return shopService.purchase(listingId, request);
    }
}
