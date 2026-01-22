package com.orchestrate.flashsale.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class OrderEvent {
    private Long orderId;
    private Long productId;
    private String status;
    private LocalDateTime orderDateTime;
}
