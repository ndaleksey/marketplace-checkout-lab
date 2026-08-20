package com.nd.checkout.inventory;

import java.util.HashMap;

/**
 * @since 2026
 */
public class UnsafeInventoryService implements InventoryService {
    private final HashMap<String, Integer> inventory = new HashMap<>();

    @Override
    public void addStock(String productId, int quantity) {
        var stockQuantity = inventory.getOrDefault(productId, 0);
        stockQuantity += quantity;
        inventory.put(productId, stockQuantity);
    }

    @Override
    public boolean reserve(String productId, int quantity) {
        if (!inventory.containsKey(productId)) {
            return false;
        }

        var availableQuantity = inventory.get(productId);

        if (availableQuantity < quantity) {
            return false;
        } else {
            availableQuantity -= quantity;
            inventory.put(productId, availableQuantity);
        }

        return true;
    }

    @Override
    public int available(String productId) {
        return inventory.get(productId);
    }
}
