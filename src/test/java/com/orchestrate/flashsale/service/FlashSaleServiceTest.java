package com.orchestrate.flashsale.service;

import com.orchestrate.flashsale.dao.OrderRepositoryDao;
import com.orchestrate.flashsale.dao.ProductRepositoryDao;
import com.orchestrate.flashsale.entities.OrderEntity;
import com.orchestrate.flashsale.entities.ProductEntity;
import com.orchestrate.flashsale.kafka.producer.KafkaProducerService;
import com.orchestrate.flashsale.mapper.OrderMapper;
import com.orchestrate.flashsale.models.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashSaleServiceTest {

    @Mock
    private ProductRepositoryDao productRepositoryDao;
    @Mock
    private OrderRepositoryDao orderRepositoryDao;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RLock rLock;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @InjectMocks
    private FlashSaleService flashSaleService;

    @Test
    void testPlaceOrder_Success() throws InterruptedException {

        Long productId = 1L;
        ProductEntity product = ProductEntity.builder()
                .productId(productId)
                .name("Phone")
                .price(new BigDecimal(500))
                .stockQuantity(10)
                .build();

        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(productRepositoryDao.findByProductId(productId)).thenReturn(Optional.of(product));
        when(orderRepositoryDao.save(any(OrderEntity.class))).thenAnswer(i -> i.getArguments()[0]);
        when(orderMapper.mapOrderEntity(any(OrderEntity.class))).thenReturn(Order.builder()
                .orderId(1L)
                .productId(productId)
                .quantity(1)
                .build());

        Order result = flashSaleService.placeOrder(productId, 1);


        assertNotNull(result);
        assertEquals(9, product.getStockQuantity()); // Stock should decrement
        verify(kafkaProducerService, times(1)).sendOrderEvent(any());
    }

    @Test
    void testPlaceOrder_OutOfStock() throws InterruptedException {

        Long productId = 1L;
        ProductEntity product = ProductEntity.builder()
                .productId(productId)
                .name("Phone")
                .price(new BigDecimal(500))
                .stockQuantity(0)
                .build();
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(productRepositoryDao.findByProductId(productId)).thenReturn(Optional.of(product));


        assertThrows(RuntimeException.class, () -> flashSaleService.placeOrder(productId, 1));
        verify(orderRepositoryDao, never()).save(any());
    }
}