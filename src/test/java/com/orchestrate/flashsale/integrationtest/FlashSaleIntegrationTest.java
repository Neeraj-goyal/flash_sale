package com.orchestrate.flashsale.integrationtest;

import com.orchestrate.flashsale.dao.ProductRepositoryDao;
import com.orchestrate.flashsale.entities.ProductEntity;
import com.orchestrate.flashsale.service.FlashSaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlashSaleIntegrationTest extends AbstractIntegrationTest{

    @Autowired
    private FlashSaleService flashSaleService;
    @Autowired
    private ProductRepositoryDao productRepository;

    @Test
    void testHighConcurrency_ShouldNotExceedStock() throws InterruptedException {
        // GIVEN: A product with only 3 items in stock
        ProductEntity product = productRepository.saveAndFlush(ProductEntity.builder()
                        .productId(1L)
                        .name("Limited Phone")
                        .stockQuantity(3)
                        .price(BigDecimal.valueOf(100))
                .build());
        Long productId = product.getProductId();

        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1); // To make all threads start at once
        AtomicInteger successfulOrders = new AtomicInteger();
        AtomicInteger failedOrders = new AtomicInteger();

        // WHEN: 10 threads try to buy at the same time
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    latch.await(); // Wait for the signal
                     flashSaleService.placeOrder(productId, 1);
                    successfulOrders.getAndIncrement();
                } catch (Throwable e) {
                    System.err.println("CRITICAL FAILURE: " + e.getMessage());
                    failedOrders.getAndIncrement();
                }
            });
        }

        latch.countDown(); // Start the race!
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // THEN:
        // 1. Successful orders must be exactly 3
        assertEquals(3, successfulOrders.get(), "Only 3 orders should succeed");
        // 2. Failed orders must be 7
        assertEquals(7, failedOrders.get(), "7 orders should fail due to out-of-stock or lock");
        // 3. Final DB stock must be 0
        ProductEntity updatedProduct = productRepository.findByProductId(productId).orElseThrow();
        assertEquals(0, updatedProduct.getStockQuantity());
    }
}
