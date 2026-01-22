package com.orchestrate.flashsale.mapper;

import com.orchestrate.flashsale.entities.OrderEntity;
import com.orchestrate.flashsale.enums.OrderStatus;
import com.orchestrate.flashsale.models.Order;
import org.springframework.stereotype.Component;


@Component
public class OrderMapper {

    public OrderEntity mapOrder(Order order){
        return OrderEntity.builder()
                .status(OrderStatus.valueOf(order.getStatus()))
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .orderDateTime(order.getOrderDateTime())
                .build();
    }

    public Order mapOrderEntity(OrderEntity orderEntity){
        return Order.builder()
                .productId(orderEntity.getProductId())
                .orderDateTime(orderEntity.getOrderDateTime())
                .status(orderEntity.getStatus().toString())
                .quantity(orderEntity.getQuantity())
                .orderId(orderEntity.getId())
                .build();
    }
}
