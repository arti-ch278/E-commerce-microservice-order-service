package com.artichourey.ecommerce.orderservice.producer;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.artichourey.ecommerce.events.OrderPlacedEvent;
import com.artichourey.ecommerce.orderservice.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
  
    public void sendOrderEvent(Order order) {

        // Build the OrderPlacedEvent here
        OrderPlacedEvent event = new OrderPlacedEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setOrderId(order.getOrderNumber());
        event.setSkuCode(order.getSkuCode());
        event.setQuantity(order.getQuantity());
        event.setEventTime(LocalDateTime.now());

        // Set userId from order (Long -> String)
        event.setUserId(String.valueOf(order.getUserId()));

        log.info("Sending order event to Kafka: orderId={}, skuCode={}, quantity={}",
                event.getOrderId(), event.getSkuCode(), event.getQuantity(), event.getUserId());

        kafkaTemplate.send("order-topic", event.getOrderId(), event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info(" Event sent successfully | orderId={}", event.getOrderId());
                } else {
                    log.error(" Failed to send event | orderId={}", event.getOrderId(), ex);
                }
            });
    }
    
}












