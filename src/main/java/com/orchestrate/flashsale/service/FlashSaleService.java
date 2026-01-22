package com.orchestrate.flashsale.service;



import com.orchestrate.flashsale.dao.OrderRepositoryDao;
import com.orchestrate.flashsale.dao.ProductRepositoryDao;
import com.orchestrate.flashsale.entities.OrderEntity;
import com.orchestrate.flashsale.kafka.dto.OrderEvent;
import com.orchestrate.flashsale.kafka.producer.KafkaProducerService;
import com.orchestrate.flashsale.mapper.OrderMapper;
import com.orchestrate.flashsale.models.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.orchestrate.flashsale.enums.OrderStatus.COMPLETED;


@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleService {
    private final ProductRepositoryDao productRepositoryDao;
    private final OrderRepositoryDao orderRepositoryDao;
    private final RedissonClient redissonClient;
    private final OrderMapper orderMapper;
    private final KafkaProducerService kafkaProducerService;

    // 1. Public method handles the LOCK (No @Transactional here)
    public Order placeOrder(Long productId, int quantity) {
        String lockKey = "product_lock_" + productId;
        var lock = redissonClient.getLock(lockKey);
        try {
            // Wait up to 10s for lock, hold for 5s
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                log.info("Lock Acquired for product: " + productId);
                // Call the transactional method
                return executeOrderTransaction(productId, quantity);
            } else {
                throw new RuntimeException("Server is Busy");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 2. Internal method handles the DB and Kafka (Needs @Transactional)
    @Transactional
    @CacheEvict(value="products", key = "#productId")
    protected Order executeOrderTransaction(Long productId, int quantity) {
        var productOpt = productRepositoryDao.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (productOpt.getStockQuantity() < quantity) {
            throw new RuntimeException("Product Out of Stock");
        }

        productOpt.setStockQuantity(productOpt.getStockQuantity() - quantity);
        productRepositoryDao.save(productOpt);

        OrderEntity newOrder = OrderEntity.builder()
                .orderDateTime(LocalDateTime.now())
                .productId(productId)
                .quantity(quantity)
                .status(COMPLETED)
                .build();

        Order orderCreated = orderMapper.mapOrderEntity(orderRepositoryDao.save(newOrder));

        kafkaProducerService.sendOrderEvent(OrderEvent.builder()
                .orderId(orderCreated.getOrderId())
                .productId(orderCreated.getProductId())
                .status(orderCreated.getStatus())
                .orderDateTime(orderCreated.getOrderDateTime())
                .build());

        return orderCreated;
    }
}
