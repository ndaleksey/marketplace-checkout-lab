package com.nd.checkout.inventory;

import java.util.HashMap;

/**
 * @since 2026
 */
public class UnsafeInventoryService implements InventoryService {
    private final HashMap<String, Integer> inventory = new HashMap<>();

    @Override
    public void addStock(String productId, int quantity) {
        inventory.put(productId, quantity);
    }

    @Override
    public boolean reserve(String productId, int quantity) {
        return inventory.containsKey(productId);
    }

    @Override
    public int available(String productId) {
        return inventory.get(productId);
    }
}
