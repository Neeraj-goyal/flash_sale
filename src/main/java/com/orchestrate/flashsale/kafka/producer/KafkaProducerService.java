package com.orchestrate.flashsale.kafka.producer;


import com.orchestrate.flashsale.kafka.dto.OrderEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {
    @Value("${kafka.topic.name}")
    private String kafkaTopic;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrderEvent(OrderEvent orderEvent){
        log.info("Producing order event to Kafka topic: {} , orderId: {}",kafkaTopic,orderEvent.getOrderId());
        CompletableFuture<SendResult<String, OrderEvent>> completableFuture=
        kafkaTemplate.send(kafkaTopic,String.valueOf(orderEvent.getOrderId()),orderEvent);

        // Handling acknowledgement asynchronously
        completableFuture.whenComplete((result,ex)->{
            if(ex==null){
                log.info("Order event sent successfully for orderId: {} , partition: {}",
                        orderEvent.getOrderId(),result.getRecordMetadata().partition());
            }else {
                log.error("Error sending order event for orderId: {} , error: {}",
                        orderEvent.getOrderId(),ex.getMessage());
            }
        });
    }
}
