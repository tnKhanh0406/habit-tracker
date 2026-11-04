package com.prj.habittracker.shop.service;

import com.prj.habittracker.shop.dto.request.PurchaseItemRequest;
import com.prj.habittracker.shop.dto.response.PurchaseItemResponse;
import com.prj.habittracker.shop.dto.response.ShopListingResponse;

import java.util.List;
import java.util.UUID;

public interface ShopService {
    List<ShopListingResponse> getListings();
    PurchaseItemResponse purchase(UUID listingId, PurchaseItemRequest request);
}
