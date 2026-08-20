package com.nd.checkout.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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
    void shouldPreserveInventoryInvariantUnderConcurrentReservations()
            throws InterruptedException, ExecutionException {
        var threads = 10;
        var initStock = 100;
        var reservationQuantity = 20;
        var productId = "product";
        var successfulReservations = new AtomicInteger();

        unsafeInventoryService.addStock(productId, initStock);

        var ready = new CountDownLatch(threads);
        var start = new CountDownLatch(1);

        try(var executor = Executors.newFixedThreadPool(threads)) {
            var futures = new ArrayList<Future<?>>();

            for (int i = 0; i < threads; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();

                    if (unsafeInventoryService.reserve(productId, reservationQuantity)) {
                        successfulReservations.incrementAndGet();
                    }

                    return null;
                }));
            }

            ready.await();
            start.countDown();

            for (var future : futures) {
                future.get();
            }

            var available = unsafeInventoryService.available(productId);
            var reserved = successfulReservations.get() * reservationQuantity;

            assertEquals(initStock, available + reserved);
        }

    }
}