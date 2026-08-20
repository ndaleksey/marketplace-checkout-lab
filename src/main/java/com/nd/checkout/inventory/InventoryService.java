package com.nd.checkout.inventory;

/**
 * @since 2026
 */
public interface InventoryService {
    void addStock(String productId, int quantity);

    boolean reserve(String productId, int quantity);

    int available(String productId);
}
