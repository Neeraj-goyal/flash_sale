package com.orchestrate.flashsale.entities;

import com.orchestrate.flashsale.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_id")
    private Long productId;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "order_date_time")
    private LocalDateTime orderDateTime;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;
}
