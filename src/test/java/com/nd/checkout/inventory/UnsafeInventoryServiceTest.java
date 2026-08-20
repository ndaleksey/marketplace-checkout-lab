package com.nd.checkout.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @since 2026
 */
class UnsafeInventoryServiceTest {
    private UnsafeInventoryService unsafeInventoryService;

    @BeforeEach
    void setUp() {
        unsafeInventoryService = new UnsafeInventoryService();
    }

    @Test
    void shouldAddStockForNewProduct() {
        unsafeInventoryService.addStock("product", 1);

        assertEquals(1, unsafeInventoryService.available("product"));
    }

    @Test
    void shouldIncreaseExistingStock() {
        unsafeInventoryService.addStock("product", 1);
        unsafeInventoryService.addStock("product", 2);

        assertEquals(3, unsafeInventoryService.available("product"));
    }

    @Test
    void shouldReserveAvailableStock() {
        unsafeInventoryService.addStock("product", 10);

        assertTrue(unsafeInventoryService.reserve("product", 10));
        assertEquals(0, unsafeInventoryService.available("product"));
    }

    @Test
    void shouldNotReserveWhenStockIsInsufficient() {
        unsafeInventoryService.addStock("product", 10);

        assertFalse(unsafeInventoryService.reserve("product", 20));
        assertEquals(10, unsafeInventoryService.available("product"));
    }

    @Test
    void shouldNotReserveUnknownProduct() {
        unsafeInventoryService.addStock("product1", 10);

        assertFalse(unsafeInventoryService.reserve("product2", 10));
    }

    @Test
    void shouldDemonstrateOversellingUnderConcurrentReservations()
            throws InterruptedException {
    }
}